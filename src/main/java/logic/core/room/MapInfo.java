package logic.core.room;


import logic.beans.Occupation;

import java.io.Serializable;
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

    public Group[][] getWarInfo() {
        return warInfo;
    }

    //每个房间该有的游戏信息
    private Group[][] warInfo;//地图上的士兵小组信息
    private int warPeriod;
    private int naturalIncrease;
    private String p1Name;
    private String p2Name;

    public MapInfo() {
        warInfo = new Group[width][distance];
        warPeriod = initPeriod;
        naturalIncrease = initNaturalIncrease;
    }

    public void setP1Name(String p1) {
        this.p1Name = p1;
    }

    public void setP2Name(String p2) {
        this.p2Name = p2;
    }

    public boolean addGroup(Group group, int row) {
        if (warInfo[row][0] == null) {
            warInfo[row][0] = group;
            return true;
        } else {
            return false;
        }
    }

    public int generateResource() {
        naturalIncrease -= decNaturalIncrease;
        return Math.max(naturalIncrease + 10, 0);
    }


    //1.先判断攻击范围内是否有敌人，如果有敌人则以最大范围的打手去攻击敌人，如果双方都能打击，则判断spd值，谁高谁先手，若相同则随机。
    //1.1攻击模式，如果是AOE，则同格内所有人受伤。非AOE则随机打击一名地方人员。
    //1.2伤害判定，如果没被闪避则，则伤害为攻击力+舞者鼓励值-巫师衰减值-敌人防御。如果闪避则造成1点伤害。
    //2.如果范围内没有敌人，则前进最小spd的距离。
    //3.如果能够碰撞到地方堡垒，则对地方堡垒造成伤害。
    public int[] nextTurn() {
        int[] damages = new int[2];
        Group tg;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < distance; j++) {
                tg = warInfo[i][j];
                if (tg != null) {
                    //1.判断有没有在攻击范围内的敌人，如果有则断定为最近的敌人
                    Group enemy = null;
                    int k;
                    for (k = 0; k < tg.maxRag; k++) {
                        if (tg.owner.equals(p1Name)) {
                            if (j + k > distance) break;
                            else {
                                enemy = warInfo[i][j + k];
                                if (enemy != null && !enemy.owner.equals(tg.owner)) {
                                    break;
                                }
                            }
                        } else {
                            if (j - k <= 0) break;
                            else {
                                enemy = warInfo[i][j - k];
                                if (enemy != null && !enemy.owner.equals(tg.owner)) {
                                    break;
                                }
                            }
                        }
                    }
                    //如果没有敌人，则前进spd，如果距离内是地方堡垒，则自爆攻击；否则就前进spd
                    if (enemy == null) {
                        if (tg.owner.equals(p1Name)) {
                            if (i + tg.minSpd > distance) damages[1] += tg.ATK;
                            else warInfo[i][j] = warInfo[i][j + tg.minSpd];
                        } else {
                            if (i - tg.minSpd <= 0) damages[0] += tg.ATK;
                            else warInfo[i][j] = warInfo[i][j - tg.minSpd];
                        }
                    } else {
                        //如果有敌人,判断是不是在地方的攻击范围之内,如果也在敌方攻击范围之内，敌人会在我方行动后进行反击
                        if (enemy.maxRag > k) {
                            if (tg.minSpd > enemy.minSpd) {
                                if (tg.owner.equals(p1Name)) onceAttack(tg, enemy, i, j + k, k);
                                else onceAttack(tg, enemy, i, j - k, k);
                                onceAttack(enemy, tg, i, j, k);
                            }
                        } else {
                            onceAttack(tg, enemy, i, j + k, k);
                        }
                    }
                }
            }
        }
        return damages;
    }

    //i，j为敌人的位置，k为二者间隔的距离
    private void onceAttack(Group my, Group enemy, int i, int j, int k) {
        for (int l = 0; l < my.humans.size(); l++) {
            Occupation oc = my.humans.get(l);
            if (oc.getRag() >= k) {
                int random = (int) (Math.random() * (enemy.humans.size() - 1));
                Occupation en = enemy.humans.get(random);
                if (oc.getAtk() >= en.getHp()) enemy.humans.remove(en);
                else en.setHp(en.getHp() - oc.getAtk());
            }
            if (enemy.humans.size() == 0) {
                warInfo[i][j] = null;
                break;
            }
        }
    }

    //一个方格内的战斗小组（可以没有人）
    public static class Group implements Serializable {
        int size;   //人数
        String owner;  //所有者
        int minSpd; //最小移速
        int maxRag; //最大攻击范围
        int HP; //总HP
        int ATK; //总ATK
        int DEF; //总DEF
        LinkedList<Occupation> humans;
    }


}
