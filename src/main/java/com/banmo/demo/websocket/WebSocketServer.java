package com.banmo.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.banmo.demo.extconfig.security.AjaxAuthSuccessHandler;
import logic.core.room.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static logic.core.RoomCenter.roomCenter;

@ServerEndpoint("/webServer/{username}")
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(AjaxAuthSuccessHandler.class);
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收username
     */
    private String username = "";

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        this.session = session;
        this.username = username;
        if (webSocketMap.containsKey(username)) {
            webSocketMap.remove(username);
            webSocketMap.put(username, this);
        } else {
            webSocketMap.put(username, this);
            addOnlineCount();
        }

        log.info("用户连接:" + username + ",当前在线人数为:" + getOnlineCount());

        JSONObject jo = new JSONObject();
        jo.put("msgType", "1");
        sendMessage(jo.toJSONString());
    }

    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(username)) {
            webSocketMap.remove(username);
            subOnlineCount();
        }
        log.info("用户退出:" + username + ",当前在线人数为:" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + username + ",报文:" + message);
        try {
            //解析发送的报文
            JSONObject jo = JSON.parseObject(message);
            System.out.println("服务器收到" + jo.toJSONString());
            String msgType = jo.getString("msgType");
            String roomID = jo.getString("roomId");
            Room room = roomCenter.getRoom(roomID);
            if (msgType.equals("1")) {
                String Stage = jo.getString("stage");
                if (Stage.equals("1")) {
                    String content = jo.getString("updateInfo");
                    room.updateCorps(username, content);
                    jo.put("corpsBody", room.getCorps(username));
                    jo.put("selectedCorpsBody", room.getSelectedCorps(username));
                    webSocketMap.get(username).sendMessage(jo.toJSONString());
                } else if (Stage.equals("2")) {
                    String content = jo.getString("strategyInfo");
                    room.updateStrategy(username, content);
                }
            } else if (msgType.equals("2")) {
                String toStage = jo.getString("toStage");
                if (toStage.equals("0")) {
                    room.gameStart();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.username + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        System.out.println("服务器发送" + message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebSocketServer getWebSocket(String username) {
        return webSocketMap.get(username);
    }

}
