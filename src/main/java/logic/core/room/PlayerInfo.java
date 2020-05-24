package logic.core.room;

import logic.beans.Corps;
import logic.beans.Occupation;
import logic.core.room.playInterface.Turn1DO;
import logic.core.room.playInterface.Turn2DO;
import logic.tool.Cloner;

import java.util.ArrayList;
import java.util.List;

//单方玩家信息，继承的两个接口分别为第一轮、第二轮游戏玩家可用的操作。
public class PlayerInfo implements Turn1DO, Turn2DO {
    static final int defaultResource = 5000;    //默认资源量
    static final int defaultMinerNum = 0;   //默认矿工数
    static final int defaultFortressHP = 300;   //默认堡垒生命值
    static final int selectedCorpsSize = 5;   //默认堡垒生命值

    private final String name; //玩家ID
    private final List<Occupation> corps;     //兵种列表，可自定义
    private final List<Occupation> selectedCorps;     //选定的兵种列表

    private Room nowRoom;
    private int resource;   //资源量
    private int minerNum;   //矿工数
    private int fortressHP; //要塞生命值


    //初始化玩家信息，包括各类资源量
    public PlayerInfo(String id) {
        this.name = id;
        this.resource = defaultResource;
        this.corps = Corps.getOneData();
        this.selectedCorps = new ArrayList<>();
        this.minerNum = defaultMinerNum;
        this.fortressHP = defaultFortressHP;
    }


    //____________________________________________turn 0
    public void setNowRoom(Room nowRoom) {
        if (nowRoom.getStage() != 0) return;
        this.nowRoom = nowRoom;
    }


    //____________________________________________turn 1
    public Occupation getOccupationByName(String name) {
        for (Occupation o : corps) {
            if (o.getName().equals(name)) return o;
        }
        for (Occupation o : selectedCorps) {
            if (o.getName().equals(name)) return o;
        }
        return null;
    }

    public boolean addHumanToCorps(String name) {
        for (Occupation o : corps) {
            if (o.getName().equals(name)) {
                if (nowRoom.getStage() != 1) return false;
                if (selectedCorps.size() < selectedCorpsSize) {
                    corps.remove(o);
                    selectedCorps.add(o);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean removeHumanFromCorps(String name) {
        for (Occupation o : corps) {
            if (nowRoom.getStage() != 1) return false;
            if (selectedCorps.size() > 0) {
                if (selectedCorps.contains(o)) {
                    corps.add(o);
                    selectedCorps.remove(o);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    //____________________________________________turn 2
    //通过职业名获取一个单位
    public Occupation getHumanByName(String name) {
        for (Occupation o : selectedCorps) {
            if (o.getName().equals(name)) {
                System.out.println("成功获取一名人类");
                return Cloner.clone(o);
            }
        }
        return null;
    }

    //通过职业id获取一个单位
    public Occupation getHumanById(int id) {
        for (Occupation o : selectedCorps) {
            if (o.getId() == id) {
                System.out.println("成功获取一名人类");
                return Cloner.clone(o);
            }
        }
        return null;
    }

    //传入单个单位，和初始位置，生产一组军队。
    public boolean produceCrops(Occupation human, int row) {
        if (nowRoom.getStage() != 3) return false;
        if (rowIsWrong(row)) {
            System.out.println("位置超出生产失败");
            return false;
        }
        if (human.getCost() > getResource()) {
            System.out.println("资源不足生产失败");
            return false;
        }

        consumeResource(human.getCost());
        return nowRoom.addGroup(name, human, row);
    }

    //传入一组单位，和初始位置，生产一组军队。
    public boolean produceCrops(List<Occupation> human, int row) {
        if (nowRoom.getStage() != 3) return false;
        if (rowIsWrong(row)) {
            System.out.println("位置超出生产失败");
            return false;
        }
        int cost = human.stream().mapToInt(Occupation::getCost).sum();
        if (cost > getResource()) {
            System.out.println("资源不足生产失败");
            return false;
        }
        consumeResource(cost);
        return nowRoom.addGroup(name, human, row);
    }

    //传入初始位置，随机选择一个单位生成。
    public void randomProduce(int row) {
        int random = (int) (Math.random() * selectedCorps.size());
        produceCrops(selectedCorps.get(random), row);
    }

    public void addOneMiner() {
        if (resource >= 150) {
            resource -= 150;
            minerNum++;
        }
    }

    public Group[][] getWarInfo() {
        return nowRoom.getWarInfo();
    }

    //其余功能函数
    public void addResource(int increase) {
        resource += increase;
    }

    public void getDamage(int damage) {
        fortressHP -= damage;
    }

    public List<Occupation> getCorps() {
        return corps;
    }

    public String getName() {
        return name;
    }

    public List<Occupation> getSelectedCorps() {
        return selectedCorps;
    }

    public int getResource() {
        return resource;
    }

    public int getMinerNum() {
        return minerNum;
    }

    public int getFortressHP() {
        return fortressHP;
    }


    private void consumeResource(int costs) {
        resource -= costs;
    }

    public boolean rowIsWrong(int row) {
        return row > 2 || row < 0;
    }
}
