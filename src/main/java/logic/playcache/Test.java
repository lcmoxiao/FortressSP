package logic.playcache;

import logic.beans.Occupation;
import logic.core.room.PlayerInfo;
import logic.core.room.Room;
import logic.core.room.playInterface.Turn1DO;
import logic.core.room.playInterface.Turn2DO;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        PlayerInfo p1 = new PlayerInfo("wo1");
        PlayerInfo p2 = new PlayerInfo("ni1");
        Room room = new Room(p1, "room", 1);
        room.join(p2);
        room.gameStart();

        System.out.println(p1.getCorps());
        String c = "Corps.get(剑士).setAtk(20);";
        room.updateCorps(p1.getName(), c);
        System.out.println(p1.getConstCorps());
    }


    void adjustCorps(Turn1DO Fortress) {
        //下面是展示在html上的代码框内容
        Fortress.getOccupationByName("剑士").setHp(50);
        Fortress.getOccupationByName("弓手").setHp(50);
        //上面是展示在html上的代码框内容
    }

    void warPlan(Turn2DO Fortress){
        Fortress.addOneMiner();
        Occupation 弓手 = Fortress.getHumanByName("弓手");
        Occupation 剑士 = Fortress.getHumanByName("剑士");

        Fortress.getWarInfo();
        Fortress.produceCrops(弓手,0);
        List<Occupation> Group = new ArrayList<>();
        Group.add(弓手);
        Group.add(剑士);
        Fortress.produceCrops(Group,1);
    }

}
