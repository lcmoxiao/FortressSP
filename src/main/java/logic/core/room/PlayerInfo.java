package logic.core.room;

import logic.beans.Corps;
import logic.beans.Occupation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//存储第一轮自定义环节的玩家信息
public class PlayerInfo {
    static final int defaultResource = 5000;    //默认资源量
    static final int defaultMinerNum = 0;   //默认矿工数
    static final int defaultFortressHP = 300;   //默认堡垒生命值
    static final int selectedCorpsSize = 5;   //默认堡垒生命值

    private String name; //玩家ID
    private Room nowRoom;
    private List<Occupation> corps;     //兵种列表，可自定义
    private Collection<Occupation> unmodifiableCorps;    //用于第二阶段的不可变兵种列表
    private List<Occupation> selectedCorps;     //选定的兵种列表
    private int resource;   //资源量
    private int minerNum;   //矿工数
    private int fortressHP; //要塞生命值

    private PlayerInfo() {
    }

    //初始化玩家信息，包括各类资源量
    public PlayerInfo(String id) {
        this.name = id;
        this.resource = defaultResource;
        this.corps = Corps.getOneData();
        this.selectedCorps = new ArrayList<>();
        this.minerNum = defaultMinerNum;
        this.fortressHP = defaultFortressHP;
    }

    public static int getDefaultResource() {
        return defaultResource;
    }

    public static int getDefaultMinerNum() {
        return defaultMinerNum;
    }

    public static int getDefaultFortressHP() {
        return defaultFortressHP;
    }

    public static int getSelectedCorpsSize() {
        return selectedCorpsSize;
    }


    //____________________________________________turn 0
    public void setNowRoom(Room nowRoom) {
        if (nowRoom.getStage() != 0) return;
        this.nowRoom = nowRoom;
    }

    //____________________________________________turn 1
    public boolean addHumanToCorps(Occupation human) {
        if (nowRoom.getStage() != 1) return false;
        if (selectedCorps.size() < selectedCorpsSize) {
            selectedCorps.add(human);
            return true;
        }
        return false;
    }

    public boolean removeHumanFromCorps(Occupation human) {
        if (nowRoom.getStage() != 1) return false;
        if (selectedCorps.size() > 0) {
            if (selectedCorps.contains(human)) {
                selectedCorps.remove(human);
                return true;
            }
        }
        return false;
    }

    //____________________________________________turn 2
    public void produceCrops(Occupation human, int row) {
        if (nowRoom.getStage() != 2) return;
        nowRoom.addGroup(this, human, row);
    }

    public void produceCrops(ArrayList<Occupation> human, int row) {
        if (nowRoom.getStage() != 2) return;
        nowRoom.addGroup(this, human, row);
    }

    public void addResource(int increase) {
        resource += increase;
    }

    public void getDamage(int damage) {
        fortressHP -= damage;
    }

    public List<Occupation> getCorps() {
        return corps;
    }

    public Collection<Occupation> getConstCorps() {
        if (unmodifiableCorps == null) unmodifiableCorps = Collections.unmodifiableList(selectedCorps);
        return unmodifiableCorps;
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
}
