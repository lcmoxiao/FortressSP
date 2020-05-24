package logic.core.room;

import logic.beans.Occupation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

//一个方格内的战斗小组（可以没有人）
public class Group implements Serializable {
    final String owner;  //所有者
    final transient List<Occupation> humans;
    int size;   //人数
    int minSpd; //最小移速
    int maxRag; //最大攻击范围
    int HP; //总HP
    int ATK; //总ATK
    int DEF; //总DEF
    transient boolean hadMoving = false;  //是否行动过

    Group(String owner, Occupation human) {
        this.owner = owner;
        this.humans = new LinkedList<>();
        this.humans.add(human);
        reFlushAttribute();
    }

    Group(String owner, List<Occupation> humans) {
        this.owner = owner;
        this.humans = humans;
        reFlushAttribute();
    }

    public String getOwner() {
        return owner;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMinSpd() {
        return minSpd;
    }

    public void setMinSpd(int minSpd) {
        this.minSpd = minSpd;
    }

    public int getMaxRag() {
        return maxRag;
    }

    public void setMaxRag(int maxRag) {
        this.maxRag = maxRag;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getATK() {
        return ATK;
    }

    public void setATK(int ATK) {
        this.ATK = ATK;
    }

    public int getDEF() {
        return DEF;
    }

    public void setDEF(int DEF) {
        this.DEF = DEF;
    }

    public void reFlushAttribute() {
        this.size = this.humans.size();
        this.minSpd = Integer.MAX_VALUE;
        this.maxRag = Integer.MIN_VALUE;
        this.HP = 0;
        this.ATK = 0;
        this.DEF = 0;
        humans.forEach(v ->
                {
                    this.minSpd = Math.min(this.minSpd, v.getSpd());
                    this.maxRag = Math.max(this.maxRag, v.getRag());
                    this.HP += v.getHp();
                    this.ATK += v.getAtk();
                    this.DEF += v.getDef();
                }
        );
    }

    @Override
    public String toString() {
        return owner + "的Group的HP:" + HP + " SPD" + minSpd;
    }


}