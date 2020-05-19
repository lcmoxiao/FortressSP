package logic.core.room;

import logic.beans.Corps;
import logic.beans.Human;

import java.util.ArrayList;

//存储第一轮自定义环节的玩家信息
public class PlayerInfo {
    static final int defaultResource = 5000;    //默认资源量
    static final int defaultMinerNum = 0;   //默认矿工数
    static final int defaultFortressHP = 300;   //默认堡垒生命值

    String name; //玩家ID

    ArrayList<Human> corps;     //兵种列表，可自定义
    ArrayList<Human> selectedCorps;     //选定的兵种列表

    int resource;   //资源量
    int minerNum;   //矿工数
    int fortressHP; //要塞生命值

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


}
