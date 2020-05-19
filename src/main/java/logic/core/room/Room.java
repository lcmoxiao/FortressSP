package logic.core.room;


import com.alibaba.fastjson.JSONObject;
import com.banmo.demo.websocket.WebSocketServer;
import logic.beans.Human;
import logic.tool.MyClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
    private GameTimer gameTimer;

    //禁止使用
    private Room() {
    }

    //开房
    public Room(PlayerInfo owner, String roomName, int id) {
        this.p1 = owner;
        this.roomName = roomName;
        this.roomId = id;
        this.isPlaying = false;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getP2Name() {
        return p2.name;
    }

    public String getP1Name() {
        return p1.name;
    }

    //加入房间
    public boolean join(PlayerInfo joiner) {
        if (!isFull()) {
            this.p2 = joiner;
            return true;
        } else return false;
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

    public void notifyAllPlayer(String msgInfo) {
        try {
            WebSocketServer.sendInfo(msgInfo, p1.name);
            WebSocketServer.sendInfo(msgInfo, p2.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ToStage对应游戏的开始，第一轮，第二轮战略，第二轮战斗  0，1，2，3
    //开始游戏
    public void gameStart() {
        mapInfo = new MapInfo();
        gameTimer = new GameTimer() {
            @Override
            void onTurn1Finished() {
                JSONObject jo = new JSONObject();
                jo.put("msgType", "2");
                jo.put("toStage", "2");
                notifyAllPlayer(jo.toJSONString());
            }

            @Override
            void onTurn2Finished() {
                JSONObject jo = new JSONObject();
                jo.put("msgType", "2");
                jo.put("toStage", "3");
                notifyAllPlayer(jo.toJSONString());
            }

            @Override
            void onTurn3Finished() {

            }
        };
        JSONObject jo = new JSONObject();
        jo.put("msgType", "2");
        jo.put("toStage", "0");
        notifyAllPlayer(jo.toJSONString());
        gameTimer.start();
        isPlaying = true;
    }

    //测试用例
//    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        PlayerInfo p = new PlayerInfo(1);
//        Room room = new Room(p,"room",1);
//        String c = "Corps.get(剑士).setAtk(20);";
//        room.setProperty(p,c);
//    }


    public ArrayList<Human> getCorps(String username) {
        if (p1.name.equals(username)) return p1.corps;
        else return p2.corps;
    }

    public ArrayList<Human> getSelectedCorps(String username) {
        if (p1.name.equals(username)) return p1.selectedCorps;
        else return p2.selectedCorps;
    }

    public void updateCorps(String username, String content) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (p1.name.equals(username)) updateCorps(p1, content);
        else updateCorps(p2, content);
    }

    private void updateCorps(PlayerInfo playerInfo, String content) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        System.out.println(playerInfo.corps);
        final String cName = "S" + playerInfo.name;
        final String preOfSet = "package logic.playcache;\n" +
                "\n" +
                "import logic.beans.Human;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "public class " + cName +
                " {\n" +
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
                "    final static int 法师 = 10;\n" +
                "\n" +
                "    ArrayList<Human> Corps;\n" +
                "\n" +
                "    public void setProperty(ArrayList<Human>corps){\n" +
                "        this.Corps = corps;\n" +
                "        //下面是展示在html上的代码框内容\n";
        final String behOfSet = "\n//上面是展示在html上的代码框内容\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";

        compile("logic.playcache." + cName, preOfSet + content + behOfSet);
        MyClassLoader myClassLoader = new MyClassLoader(outDir);
        Class c = myClassLoader.loadClass("logic.playcache." + cName);
        Object o = c.getDeclaredConstructor().newInstance();
        try {
            Method method = c.getMethod("setProperty", playerInfo.corps.getClass());
            method.invoke(o, playerInfo.corps);
            System.out.println(playerInfo.corps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //添加单个士兵
    public boolean addGroup(PlayerInfo playerInfo, Human human, int row) {
        if (human.getCost() > playerInfo.resource) return false;
        else {
            MapInfo.Group g = new MapInfo.Group();
            g.size = 1;
            g.owner = playerInfo.name;
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
    public boolean addGroup(PlayerInfo playerInfo, ArrayList<Human> humans, int row) {
        int cost = humans.stream().mapToInt(Human::getCost).sum();
        if (cost > playerInfo.resource) return false;
        else {
            MapInfo.Group g = new MapInfo.Group();
            g.size = humans.size();
            g.owner = playerInfo.name;
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
    private void nextTurn() {
        //ToDo
        //1.资源增长（自然+挖矿）
        //2.生产小队（）
        //3.地图信息刷新（小组移动，小组战斗，小组自爆）

    }

    //房间是不是加过人了
    public boolean isFull() {
        return p2 != null;
    }

    //是否开始对局
    public boolean isPlaying() {
        return isPlaying;
    }


}
