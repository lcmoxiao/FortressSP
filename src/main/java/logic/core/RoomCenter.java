package logic.core;

import logic.core.room.PlayerInfo;
import logic.core.room.Room;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


//房间管理中心，也是所有数据的存储中心，所有游戏相关都放在rooms里。
public class RoomCenter {

    private static final HashMap<String, Room> rooms = new HashMap<>();
    public static RoomCenter roomCenter;
    static Integer randomId = 0;//用来给房间赋予id

    static {
        roomCenter = new RoomCenter();
    }

    //传入 房主的 id，和房间名
    public int createRoom(String playerId, String roomName) {
        PlayerInfo pl = new PlayerInfo(playerId);
        Room r = new Room(pl, roomName);
        rooms.put(String.valueOf(randomId), r);
        return randomId++;
    }

    public boolean joinRoom(String playerId, String roomId) throws IOException {
        PlayerInfo pl = new PlayerInfo(playerId);
        return rooms.get(roomId).join(pl);
    }


    public Map<String, String> getRoomInfos() {
        Map<String, String> s = new HashMap<>();
        rooms.forEach((key, value) -> s.put(key, value.getRoomName()));
        return s;
    }

    public String getRoomName(String roomId) {
        return rooms.get(roomId).getRoomName();
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Set<String> getRoomIds() {
        return rooms.keySet();
    }

}
