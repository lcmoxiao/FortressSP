package logic.core.room;


import logic.beans.Occupation;


//存储第二轮战斗时所有信息
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
    private final Group[][] warInfo;//地图上的士兵小组信息
    private int warPeriod;
    private int naturalIncrease;
    private final Room nowRoom;

    public MapInfo(Room nowRoom) {
        warInfo = new Group[width][distance];
        warPeriod = initPeriod;
        naturalIncrease = initNaturalIncrease;
        this.nowRoom = nowRoom;
    }


//    public static void main(String[] args) {
//        MapInfo mapInfo = new MapInfo();
//        mapInfo.setP1Name("a");
//        mapInfo.setP2Name("b");
//
//        List<Occupation> corps = Corps.getOneData();
//
//        LinkedList<Occupation> corpsss = new LinkedList<>();
//        corpsss.add(Cloner.clone(corps.get(0)));
//        corpsss.add(Cloner.clone(corps.get(0)));
//        corpsss.add(Cloner.clone(corps.get(0)));
//        corpsss.add(Cloner.clone(corps.get(0)));
//        Group g1 = new Group(mapInfo.p1Name, corpsss);
//        mapInfo.addGroup(g1, 0);
//
//        LinkedList<Occupation> corpss = new LinkedList<>();
//        corpss.add(Cloner.clone(corps.get(10)));
//        corpss.add(Cloner.clone(corps.get(10)));
//        Group g2 = new Group(mapInfo.p2Name, corpss);
//        mapInfo.addGroup(g2, 0);
//
//        System.out.println(JSON.toJSONString(mapInfo.getWarInfo()));
//
////        for (int i = 0; i < 10; i++) {
////            System.out.println("____"+JSON.toJSONString(mapInfo.getWarInfo()[0][0]));
////            mapInfo.nextTurn();
////        }
//
//    }

    public Group[][] getWarInfo() {
        return warInfo;
    }

    public boolean addGroup(Group group, int row) {
        if (group.owner.equals(nowRoom.getP1Name())) {
            if (warInfo[row][0] == null) {
                warInfo[row][0] = group;
                return true;
            } else {
                System.out.println("war[" + row + "][" + 0 + "]" + "已被占据");
                return false;
            }
        } else {
            if (warInfo[row][distance - 1] == null) {
                warInfo[row][distance - 1] = group;
                return true;
            } else {
                System.out.println("war[" + row + "][" + (distance - 1) + "]" + "已被占据");
                return false;
            }
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
                    if (!tg.hadMoving) {
                        tg.hadMoving = true;
                    } else {
                        continue;
                    }

                    //1.判断有没有在攻击范围内的敌人，如果有则断定为最近的敌人
                    Group enemy = null;
                    int k;
                    for (k = 1; k <= tg.maxRag; k++) {
                        if (tg.owner.equals(nowRoom.getP1Name())) {
                            if (j + k > distance) {
                                System.out.println("已经到头了，p1没找到敌人");
                                break;
                            } else {
                                enemy = warInfo[i][j + k];
                                if (enemy != null && !enemy.owner.equals(tg.owner)) {
                                    System.out.println("p1的敌人在" + "i:" + i + " j+k:" + (j + k));
                                    break;
                                }
                                enemy = null;
                            }
                        } else {
                            if (j - k <= 0) {
                                System.out.println("已经到头了，p2没找到敌人");
                                break;
                            } else {
                                enemy = warInfo[i][j - k];
                                if (enemy != null && !enemy.owner.equals(tg.owner)) {
                                    System.out.println("p2的敌人在" + "i:" + i + " j-k:" + (j - k));
                                    break;
                                }
                                enemy = null;
                            }
                        }
                    }
                    //如果没有敌人，则前进spd，如果距离内是地方堡垒，则自爆攻击；否则就前进spd
                    if (enemy == null) {
                        System.out.println("没有敌人");
                        if (tg.owner.equals(nowRoom.getP1Name())) {
                            if (j + tg.minSpd >= distance) {
                                System.out.println("给p2堡垒造成伤害" + tg.ATK);
                                damages[1] += tg.ATK;
                            } else {
                                for (int l = tg.minSpd; l > 0; l--) {
                                    if (warInfo[i][j + l] == null) {
                                        System.out.println("p1前进" + l);
                                        warInfo[i][j + l] = warInfo[i][j];
                                        warInfo[i][j] = null;
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (j - tg.minSpd < 0) {
                                System.out.println("给p1堡垒造成伤害" + tg.ATK);
                                damages[0] += tg.ATK;
                            } else {
                                for (int l = tg.minSpd; l > 0; l--) {
                                    if (warInfo[i][j - l] == null) {
                                        System.out.println("p2前进" + l);
                                        warInfo[i][j - l] = warInfo[i][j];
                                        warInfo[i][j] = null;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        //如果有敌人,判断是不是在地方的攻击范围之内,如果也在敌方攻击范围之内，敌人会在我方行动后进行反击
                        System.out.println("我方的攻击范围是" + tg.maxRag + "敌人的攻击范围是" + enemy.maxRag + " 我方距离敌方的距离是" + k);
                        if (enemy.maxRag >= k) {
                            if (tg.minSpd >= enemy.minSpd) {
                                System.out.println("我方先手");
                                if (tg.owner.equals(nowRoom.getP1Name())) onceAttack(tg, enemy, i, j + k, k);
                                else onceAttack(tg, enemy, i, j - k, k);
                                //如果敌人还活着
                                if (enemy.size != 0) {
                                    System.out.println("敌方反击");
                                    onceAttack(enemy, tg, i, j, k);
                                }
                            } else {
                                System.out.println("敌方先手");
                                onceAttack(enemy, tg, i, j, k);
                                //如果我方还活着
                                if (tg.size != 0) {
                                    System.out.println("我方反击");
                                    if (tg.owner.equals(nowRoom.getP1Name())) onceAttack(tg, enemy, i, j + k, k);
                                    else onceAttack(tg, enemy, i, j - k, k);
                                }
                            }
                        } else {
                            System.out.println("我方手长");
                            if (tg.owner.equals(nowRoom.getP1Name())) onceAttack(tg, enemy, i, j + k, k);
                            else onceAttack(tg, enemy, i, j - k, k);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < distance; j++) {
                tg = warInfo[i][j];
                if (tg != null) {
                    tg.hadMoving = false;
                }
            }
        }
        return damages;
    }

    //i，j为敌人的位置，k为二者间隔的距离
    private void onceAttack(Group my, Group enemy, int i, int j, int k) {
        for (int l = 0; l < my.humans.size(); l++) {
            Occupation oc = my.humans.get(l);
            int ocAtk = oc.getAtk();
            if (oc.getRag() >= k) {
                if (!oc.isAOE()) {
                    int random = (int) (Math.random() * (enemy.humans.size()));
                    Occupation en = enemy.humans.get(random);
                    int enHp = en.getHp();
                    if (en.getSpd() < Math.random()) {
                        System.out.println("哎呀，攻击被" + enemy.owner + "的" + random + "号单位闪避了");
                    } else {
                        System.out.println(enemy.owner + "的" + random + "号单位受到了攻击，原血量为" + enHp);
                        int restHp = enHp - ocAtk;
                        System.out.println(enemy.owner + "的" + random + "号单位剩余血量= " + enHp + "-" + ocAtk + "=" + restHp);
                        if (restHp <= 0) {
                            System.out.println(enemy.owner + "的" + random + "号单位死掉了");
                            enemy.humans.remove(en);
                        } else {
                            en.setHp(restHp);
                        }
                    }
                } else {
                    System.out.println(my.owner + "的" + l + "号单位是AOE攻击,无法被闪避");
                    for (int m = 0; m < enemy.humans.size(); m++) {
                        Occupation en = enemy.humans.get(m);
                        int enHp = en.getHp();
                        System.out.println(enemy.owner + "的" + m + "号单位受到了攻击，原血量为" + enHp);
                        int restHp = enHp - ocAtk;
                        System.out.println(enemy.owner + "的" + m + "号单位剩余血量= " + enHp + "-" + ocAtk + "=" + restHp);
                        if (restHp <= 0) {
                            System.out.println(enemy.owner + "的" + m + "号单位死掉了");
                            enemy.humans.remove(en);
                        } else {
                            en.setHp(restHp);
                        }
                    }
                }
            }
            enemy.reFlushAttribute();
            if (enemy.size == 0) {
                warInfo[i][j] = null;
                break;
            }
        }
    }


}
