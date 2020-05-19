package logic.beans;


//    enum type {
//        剑士(0),盾战(1),狂战(2),骑兵(3),牧师(4),舞者(5),巫师(6),弓手(7),枪手(8),重炮(9),法师(10);
//        int value;
//        type(int i) {
//            this.value = i;
//        }
//    }

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


//仅提供修改ATK，DEF，HP的方法，可以获取所有的信息
public class Human implements Serializable {
    final static int 剑士 = 1;
    final static int 盾战 = 2;
    final static int 狂战 = 3;
    final static int 骑兵 = 4;
    final static int 牧师 = 5;
    final static int 舞者 = 6;
    final static int 巫师 = 7;
    final static int 弓手 = 8;
    final static int 枪手 = 9;
    final static int 重炮 = 10;
    final static int 法师 = 11;
    final static int 战车 = 12;

    @JSONField(ordinal=1)
    int id;
    @JSONField(ordinal=2)
    String name;  //中文名  1
    @JSONField(ordinal=3)
    int hp;       //血量  2
    @JSONField(ordinal=4)
    int atk;      //攻击  3
    @JSONField(ordinal=5)
    int def;      //防御  4
    @JSONField(ordinal=6)
    int spd;      //移速  6
    @JSONField(ordinal=7)
    double dod;   //闪避率  7
    @JSONField(ordinal=8)
    int rag;      //攻击范围  8
    @JSONField(ordinal=9)
    int cost;     //价格   9
    @JSONField(ordinal=10)
    boolean isAOE;  //范围攻击   10

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSpd() {
        return spd;
    }
    public double getDod() {
        return dod;
    }

    public int getRag() {
        return rag;
    }

    public int getCost() {
        return cost;
    }

    public boolean isAOE() {
        return isAOE;
    }

    public Human(int id, String name, int HP, int ATK, int DEF, int SPD, double DOD, int RAG, int cost, boolean isAOE) {
        this.id = id;
        this.name = name;
        this.hp = HP;
        this.atk = ATK;
        this.def = DEF;
        this.spd = SPD;
        this.rag = RAG;
        this.dod = DOD;
        this.isAOE = isAOE;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Human{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hp=" + hp +
                ", atk=" + atk +
                ", def=" + def +
                ", spd=" + spd +
                ", dod=" + dod +
                ", rag=" + rag +
                ", cost=" + cost +
                ", isAOE=" + isAOE +
                '}';
    }

    @Override
    protected Human clone() throws CloneNotSupportedException {
        return (Human) super.clone();
    }
}
