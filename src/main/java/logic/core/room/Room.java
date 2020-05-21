package logic.core.room;

import com.alibaba.fastjson.JSONObject;
import com.banmo.demo.websocket.WebSocketServer;
import logic.beans.Corps;
import logic.beans.Occupation;
import logic.tool.MyClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static logic.tool.MyCompiler.compile;
import static logic.tool.MyCompiler.outDir;


public class Room {


    //房间ID和房间名
    private int roomId; //房间id，由Center自动传入
    private String roomName; //房间名
    private boolean isPlaying;  //对局是否开始
    private boolean isFinished; //对局是否结束
    //房间内的两个玩家
    private PlayerInfo p1;
    private PlayerInfo p2;
    //共用一张map信息
    private MapInfo mapInfo;
    //GameTimer
    private final GameTimer gameTimer = new GameTimer() {
        @Override
        void onTurn1Finished() {
            System.out.println("即将开始布局");
            JSONObject jo = new JSONObject();
            jo.put("msgType", "2");
            jo.put("toStage", "2");
            jo.put("p1SelectedCorpsInfo", p1.getSelectedCorps());
            jo.put("p2SelectedCorpsInfo", p2.getSelectedCorps());
            generateClass(p1, "", 2);
            generateClass(p2, "", 2);
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

    //禁止使用
    private Room() {
    }

    public Room(PlayerInfo owner, String roomName, int id) {
        create(owner, roomName, id);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getP2Name() {
        if (p2 != null) return p2.getName();
        else return p1.getName();
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
        jo.put("corpsBody", Corps.getOneData());
        notifyAllPlayer(jo.toJSONString());
        gameTimer.start();
        isPlaying = true;
    }

    //创建房间
    public void create(PlayerInfo owner, String roomName, int id) {
        this.p1 = owner;
        p1.setNowRoom(this);
        mapInfo = new MapInfo();
        mapInfo.setP1Name(getP1Name());
        this.roomName = roomName;
        this.roomId = id;
        this.isPlaying = false;
    }

    //加入房间
    public boolean join(PlayerInfo joiner) throws IOException {
        if (!isFull()) {
            this.p2 = joiner;
            p2.setNowRoom(this);
            mapInfo.setP2Name(getP2Name());
            //给p1发送自己的名字p2name
            JSONObject jo = new JSONObject();
            jo.put("msgType", "0");
            jo.put("p2Name", getP2Name());
            WebSocketServer.sendInfo(jo.toJSONString(), getP1Name());
            return true;
        } else return false;
    }

    public void notifyAllPlayer(String msgInfo) {
        try {
            WebSocketServer.sendInfo(msgInfo, getP1Name());
            WebSocketServer.sendInfo(msgInfo, getP2Name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Occupation> getCorps(String username) {
        if (getP1Name().equals(username)) return p1.getCorps();
        else return p2.getCorps();
    }

    public List<Occupation> getSelectedCorps(String username) {
        if (getP1Name().equals(username)) return p1.getSelectedCorps();
        else return p2.getSelectedCorps();
    }

    public MapInfo.Group[][] getWarInfo() {
        return mapInfo.getWarInfo();
    }

    //添加单个士兵
    public boolean addGroup(PlayerInfo playerInfo, Occupation human, int row) {
        if (row > 2 || row < 0) return false;
        if (human.getCost() > playerInfo.getResource()) return false;
        else {
            MapInfo.Group g = new MapInfo.Group();
            g.size = 1;
            g.owner = playerInfo.getName();
            g.minSpd = human.getSpd();
            g.maxRag = human.getRag();
            g.HP = human.getHp();
            g.ATK = human.getAtk();
            g.DEF = human.getDef();
            mapInfo.addGroup(g, row);
            return true;
        }
    }

    //添加小组士兵
    public boolean addGroup(PlayerInfo playerInfo, List<Occupation> humans, int row) {
        if (row > 2 || row < 0) return false;
        int cost = humans.stream().mapToInt(Occupation::getCost).sum();
        if (cost > playerInfo.getResource()) return false;
        else {
            MapInfo.Group g = new MapInfo.Group();
            g.size = humans.size();
            g.owner = playerInfo.getName();
            g.minSpd = Integer.MAX_VALUE;
            g.maxRag = Integer.MIN_VALUE;
            g.HP = 0;
            g.ATK = 0;
            g.DEF = 0;
            humans.forEach(v ->
                    {
                        g.minSpd = Math.min(g.minSpd, v.getSpd());
                        g.maxRag = Math.max(g.maxRag, v.getRag());
                        g.HP += v.getHp();
                        g.ATK += v.getAtk();
                        g.DEF += v.getDef();
                    }
            );
            mapInfo.addGroup(g, row);
            return true;
        }
    }

    //下一回合
    private void nextTurn() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        //ToDo
        //1.资源增长（自然+挖矿）
        int natureGenerate = mapInfo.generateResource();
        p1.addResource(natureGenerate + p1.getMinerNum() * 150);
        p2.addResource(natureGenerate + p2.getMinerNum() * 150);
        //2.生产小队（）
        MyClassLoader myClassLoader = new MyClassLoader(outDir);
        Class c = myClassLoader.loadClass("logic.playcache." + "S" + getP1Name());
        Object o = c.getDeclaredConstructor().newInstance();
        Method method = c.getMethod("warPlan", p1.getClass());
        method.invoke(o, p1);
        c = myClassLoader.loadClass("logic.playcache." + "S" + getP2Name());
        o = c.getDeclaredConstructor().newInstance();
        method = c.getMethod("warPlan", p2.getClass());
        method.invoke(o, p2);
        //3.地图信息刷新（小组移动，小组战斗，小组自爆）
        int[] damages = new int[2];
        damages = mapInfo.nextTurn();
        p1.getDamage(damages[0]);
        p1.getDamage(damages[1]);

        JSONObject jo = new JSONObject();
        jo.put("warInfo", mapInfo.getWarInfo());
        jo.put("p1Resource", p1.getResource());
        jo.put("p1FortressHP", p1.getFortressHP());
        jo.put("p1MinerNum", p1.getMinerNum());
        jo.put("p2Resource", p2.getResource());
        jo.put("p2FortressHP", p2.getFortressHP());
        jo.put("p2MinerNum", p2.getMinerNum());
        notifyAllPlayer(jo.toJSONString());
    }

    //房间是不是加过人了
    public boolean isFull() {
        return p2 != null;
    }

    //是否开始对局
    public boolean isPlaying() {
        return isPlaying;
    }

    private void generateClass(PlayerInfo playerInfo, String content, int stage) {
        final String cName = "S" + playerInfo.getName();
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

    public int getStage() {
        return gameTimer.getStage();
    }

    public void updateCorps(String username, String content) {
        PlayerInfo playerInfo;
        if (p1.getName().equals(username)) playerInfo = p1;
        else playerInfo = p2;
        final String cName = "S" + playerInfo.getName();
        int stage = getStage();
        try {
            generateClass(playerInfo, content, stage);
            MyClassLoader myClassLoader = new MyClassLoader(outDir);
            Class c = myClassLoader.loadClass("logic.playcache." + cName);
            Object o = c.getDeclaredConstructor().newInstance();
            Method method = c.getMethod("adjustCorps", playerInfo.getClass());
            method.invoke(o, playerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStrategy(String username, String content) {
        PlayerInfo playerInfo;
        if (p1.getName().equals(username)) playerInfo = p1;
        else playerInfo = p2;
        int stage = getStage();
        generateClass(playerInfo, content, stage);
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

}
