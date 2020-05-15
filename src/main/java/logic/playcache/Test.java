package logic.playcache;


import logic.beans.Human;

import java.util.ArrayList;

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

    ArrayList<Human> Corps;

    void setProperty(ArrayList<Human>corps){
        this.Corps = corps;
        //下面是展示在html上的代码框内容
        Corps.get(剑士).setAtk(10);
        Corps.get(法师).setAtk(12);


        //上面是展示在html上的代码框内容
    }



}
