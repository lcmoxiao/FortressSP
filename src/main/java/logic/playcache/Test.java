package logic.playcache;


import logic.beans.Occupation;
import logic.core.room.PlayerInfo;
import logic.core.room.Room;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Test {

    final static int 剑士 = 0;
    final static int 盾战 = 1;
    final static int 狂战 = 2;
    final static int 骑兵 = 3;
    final static int 牧师 = 4;
    final static int 舞者 = 5;
    final static int 巫师 = 6;
    final static int 弓手 = 7;
    final static int 枪手 = 8;
    final static int 重炮 = 9;
    final static int 法师 = 10;

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PlayerInfo p1 = new PlayerInfo("wo1");
        PlayerInfo p2 = new PlayerInfo("ni1");
        Room room = new Room(p1, "room", 1);
        room.join(p2);
        room.gameStart();

        new Test().setProperty(p1);
        System.out.println(p1.getCorps());
        String c = "Corps.get(剑士).setAtk(20);";
        room.updateCorps(p1.getName(), c);
        System.out.println(p1.getConstCorps());
    }

    void setProperty(PlayerInfo Fortress) {
        List<Occupation> Corps = Fortress.getCorps();
        //下面是展示在html上的代码框内容
        Corps.get(剑士).setAtk(15);
        Corps.get(法师).setAtk(12);
        //上面是展示在html上的代码框内容
    }

    void adjustCorps(PlayerInfo Fortress) {
        List<Occupation> Corps = (List<Occupation>) Fortress.getConstCorps();
        //下面是展示在html上的代码框内容
        Fortress.addHumanToCorps(Corps.get(剑士));
        Fortress.addHumanToCorps(Corps.get(法师));
        Fortress.removeHumanFromCorps(Corps.get(法师));
        //上面是展示在html上的代码框内容
    }

}
