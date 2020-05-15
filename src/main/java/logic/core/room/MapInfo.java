package logic.core.room;



import logic.beans.Human;

import java.util.ArrayList;
import java.util.LinkedList;


//存储第二轮战斗时信息
public class MapInfo {
    final static int distance = 20;  //地图距离
    final static int width = 3;  //地图距离
    final static int initPeriod = 8; //8s一个战斗周期
    final static int minPeriod = 1; //最低为1s一个战斗周期
    final static int decFrequency = 10;//每十轮战斗周期减一秒
    final static int initNaturalIncrease = 500; //每一轮加500资源
    final static int minNaturalIncrease = 0; //每一轮加500资源
    final static int decNaturalIncrease = 10; //每轮-10，最低为0；
    //每个房间该有的游戏信息
    private Group[][] warInfo;//地图上的士兵小组信息
    private int warPeriod;
    private int naturalIncrease;

    public MapInfo() {
        warInfo = new Group[width][distance];
        warPeriod = initPeriod;
        naturalIncrease = initNaturalIncrease;
    }

    public boolean addGroup(Group group, int row) {
        if (warInfo[row][0] == null) {
            warInfo[row][0] = group;
            return true;
        } else {
            return false;
        }
    }

    //一个方格内的战斗小组（可以没有人）
    static class Group {
        int size;   //人数
        String  owner;  //所有者
        int minSpd; //最小移速
        int maxRag; //最大攻击范围
        int HP; //总HP
        int ATK; //总ATK
        int DEF; //总DEF
        LinkedList<Human> humans;
    }


}
