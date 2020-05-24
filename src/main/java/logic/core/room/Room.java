package logic.core.room;

import com.alibaba.fastjson.JSONObject;
import com.banmo.demo.websocket.WebSocketServer;
import logic.beans.Corps;
import logic.beans.Occupation;
import logic.core.room.playInterface.Turn1DO;
import logic.core.room.playInterface.Turn2DO;
import logic.tool.MyClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static logic.tool.MyCompiler.compile;
import static logic.tool.MyCompiler.outDir;

//一个游戏房间，承载了这一局游戏的所有内容
public class Room {

    //房间ID和房间名
    private String roomName; //房间名
    private int turn = 0;//游戏回合数
    //房间内的两个玩家
    private PlayerInfo p1;
    private PlayerInfo p2;
    private MapInfo mapInfo; //共用一张map信息
    //游戏回合计时器
    private final GameTimer gameTimer = new GameTimer() {
        @Override
        void onTurn1Finished() {
            System.out.println("即将开始布局");
            JSONObject jo = new JSONObject();
            jo.put("msgType", "2");
            jo.put("toStage", "2");
            generateClass("S" + getP1Name(), "", 2);
            generateClass("S" + getP2Name(), "", 2);
            notifyAllPlayer(jo.toJSONString());
        }

        @Override
        void onTurn2Finished() {
            System.out.println("即将开始战斗");
            JSONObject jo = new JSONObject();
            jo.put("msgType", "2");
            jo.put("toStage", "3");
            notifyAllPlayer(jo.toJSONString());
        }

        @Override
        void onTurn3Finished() {
            System.out.println("开始战斗");
            TimerTask continueTimer = new TimerTask() {
                @Override
                public void run() {
                    try {
                        nextTurn();
                        if (p1.getFortressHP() < 0) {
                            JSONObject jo = new JSONObject();
                            jo.put("msgType", "3");
                            jo.put("info", "P1获胜");
                            notifyAllPlayer(jo.toJSONString());
                            cancel();
                        } else if (p2.getFortressHP() < 0) {
                            JSONObject jo = new JSONObject();
                            jo.put("msgType", "3");
                            jo.put("info", "P2获胜");
                            notifyAllPlayer(jo.toJSONString());
                            cancel();
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Timer().schedule(continueTimer, 0, MapInfo.initPeriod * 1000);
        }
    };

    public Room(PlayerInfo owner, String roomName) {
        create(owner, roomName);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getP2Name() {
        if (p2 != null) return p2.getName();
        else {
            System.out.println("p2尚未加入");
            return getP1Name();
        }
    }

    public String getP1Name() {
        return p1.getName();
    }

    //ToStage对应游戏的开始，第一轮，第二轮战略，第二轮战斗  0，1，2，3
    //开始游戏
    public void gameStart() {
        JSONObject jo = new JSONObject();
        jo.put("msgType", "2");
        jo.put("toStage", "1");
        notifyAllPlayer(jo.toJSONString());
        jo = new JSONObject();
        jo.put("msgType", "1");
        jo.put("stage", "1");
        jo.put("corpsBody", Corps.getOneData());
        notifyAllPlayer(jo.toJSONString());
        gameTimer.start();
    }

    //创建房间
    public void create(PlayerInfo owner, String roomName) {
        this.p1 = owner;
        p1.setNowRoom(this);
        mapInfo = new MapInfo(this);
        this.roomName = roomName;
    }

    //加入房间
    public boolean join(PlayerInfo joiner) {
        if (!isFull()) {
            this.p2 = joiner;
            p2.setNowRoom(this);
            //给p1发送自己的名字p2name
            JSONObject jo = new JSONObject();
            jo.put("msgType", "0");
            jo.put("p2Name", getP2Name());
            WebSocketServer.sendInfo(jo.toJSONString(), getP1Name());
            return true;
        } else return false;
    }

    public List<Occupation> getCorps(String username) {
        if (getP1Name().equals(username)) return p1.getCorps();
        else return p2.getCorps();
    }

    public List<Occupation> getSelectedCorps(String username) {
        if (getP1Name().equals(username)) return p1.getSelectedCorps();
        else return p2.getSelectedCorps();
    }

    public Group[][] getWarInfo() {
        return mapInfo.getWarInfo();
    }

    //添加单个士兵
    public boolean addGroup(String owner, Occupation human, int row) {
        Group g = new Group(owner, human);
        return mapInfo.addGroup(g, row);
    }

    //添加小组士兵
    public boolean addGroup(String owner, List<Occupation> humans, int row) {
        Group g = new Group(owner, humans);
        return mapInfo.addGroup(g, row);
    }

    //房间是不是加过人了
    public boolean isFull() {
        return p2 != null;
    }

    public int getStage() {
        return gameTimer.getStage();
    }

    public void updateCorps(String username, String content) {
        PlayerInfo playerInfo;
        if (getP1Name().equals(username)) playerInfo = p1;
        else playerInfo = p2;
        final String cName = "S" + username;
        int stage = getStage();
        try {
            generateClass(cName, content, stage);
            MyClassLoader myClassLoader = new MyClassLoader(outDir);
            Class c = myClassLoader.loadClass("logic.playcache." + cName);
            Object o = c.getDeclaredConstructor().newInstance();
            Method method = c.getMethod("adjustCorps", Turn1DO.class);
            method.invoke(o, playerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStrategy(String username, String content) {
        final String cName = "S" + username;
        int stage = getStage();
        generateClass(cName, content, stage);
    }

    //请出房间
    public boolean plzOut() {
        if (isFull()) {
            this.p2 = null;
            return true;
        } else return false;
    }

    //p2退出房间
    public boolean exit() {
        if (isFull()) {
            this.p2 = null;
            return true;
        } else return false;
    }

    private void notifyAllPlayer(String msgInfo) {
        WebSocketServer.sendInfo(msgInfo, getP1Name());
        WebSocketServer.sendInfo(msgInfo, getP2Name());
    }

    //下一回合
    private void nextTurn() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        //1.资源增长（自然+挖矿）
        int natureGenerate = mapInfo.generateResource();
        p1.addResource(natureGenerate);
        p2.addResource(natureGenerate);
        //2.地图信息刷新（小组移动，小组战斗，小组自爆）
        int[] damages;
        damages = mapInfo.nextTurn();
        p1.getDamage(damages[0]);
        p2.getDamage(damages[1]);
        //3.生产小队（）
        MyClassLoader myClassLoader = new MyClassLoader(outDir);
        Class c = myClassLoader.loadClass("logic.playcache." + "S" + getP1Name());
        Object o = c.getDeclaredConstructor().newInstance();
        Method method = c.getMethod("warPlan", Turn2DO.class);
        method.invoke(o, p1);
        c = myClassLoader.loadClass("logic.playcache." + "S" + getP2Name());
        o = c.getDeclaredConstructor().newInstance();
        method = c.getMethod("warPlan", Turn2DO.class);
        method.invoke(o, p2);
        JSONObject jo = new JSONObject();
        jo.put("warInfo", mapInfo.getWarInfo());
        jo.put("p1Resource", p1.getResource());
        jo.put("p1FortressHP", p1.getFortressHP());
        jo.put("p1MinerNum", p1.getMinerNum());
        jo.put("p2Resource", p2.getResource());
        jo.put("p2FortressHP", p2.getFortressHP());
        jo.put("p2MinerNum", p2.getMinerNum());
        jo.put("turn", "" + turn);
        jo.put("msgType", "1");
        jo.put("stage", "3");
        notifyAllPlayer(jo.toJSONString());
        turn++;
    }

    private void generateClass(String cName, String content, int stage) {
        final String preOfSet = "package logic.playcache;\n" +
                "\n" +
                "import logic.beans.Occupation;\n" +
                "import logic.core.room.PlayerInfo;\n" +
                "import logic.core.room.Room;\n" +
                "import logic.core.room.playInterface.Turn1DO;\n" +
                "import logic.core.room.playInterface.Turn2DO;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;" +
                "\n" +
                "public class " + cName + " {\n" +
                "\n" +
                "    final static int 剑士 = 0;\n" +
                "    final static int 盾战 = 1;\n" +
                "    final static int 狂战 = 2;\n" +
                "    final static int 骑兵 = 3;\n" +
                "    final static int 牧师 = 4;\n" +
                "    final static int 舞者 = 5;\n" +
                "    final static int 巫师 = 6;\n" +
                "    final static int 弓手 = 7;\n" +
                "    final static int 枪手 = 8;\n" +
                "    final static int 重炮 = 9;\n" +
                "    final static int 法师 = 10;\n";

        final String s1pre = "  public void adjustCorps(Turn1DO Fortress){\n";

        final String s2pre = "  public void warPlan(Turn2DO Fortress){\n";

        final String behOfSet = " \n//上面是展示在html上的代码框内容\n" +
                "    }\n" +
                "}";
        if (stage == 1) {
            compile("logic.playcache." + cName, preOfSet + s1pre + content + behOfSet);
        } else if (stage == 2 || stage == 3) compile("logic.playcache." + cName, preOfSet + s2pre + content + behOfSet);
    }

}
