package com.banmo.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.banmo.demo.websocket.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static logic.core.RoomCenter.roomCenter;

@Controller
@RequestMapping("/roomList")
public class RoomListController {

    @GetMapping("/show")
    public String showRoomList(Model model) {
        model.addAttribute("RoomIds", roomCenter.getRoomIds());
        return "/room/roomList";
    }

    //房主创建房间，之后跳转到room房间等待websocket传递新信息。
    @PostMapping("/add")
    public String createRoom(@RequestParam(name = "roomName") String roomName,
                             HttpSession session) {
        String p1Name = (String) session.getAttribute("username");
        //记录房间名
        session.setAttribute("roomName", roomName);
        session.setAttribute("roomId", roomCenter.createRoom(p1Name, roomName));
        session.setAttribute("p1Name", p1Name);
        session.setAttribute("role", "p1");
        return "room/room";
    }

    //玩家加入房间，同时通过websocket给房主发送信息。
    @PostMapping("/join")
    public String joinRoom(Model model, @RequestParam(name = "roomId") String roomId,
                           HttpSession session) throws IOException {
        System.out.println("joinRoom");
        if (!roomCenter.getRoomIds().contains(roomId)) {
            System.out.println("房间不存在");
            model.addAttribute("RoomIds", roomCenter.getRoomIds());
            model.addAttribute("errorInfo", "房间不存在");
            return "/room/roomList";
        } else {
            //获取本地的name
            String p2Name = (String) session.getAttribute("username");
            //记录p1的name
            String p1Name = roomCenter.getRoom(roomId).getP1Name();
            if (roomCenter.joinRoom(p2Name, roomId)) {
                //记录房间号
                session.setAttribute("roomId", roomId);
                session.setAttribute("roomName", roomCenter.getRoomName(roomId));
                session.setAttribute("p1Name", p1Name);
                session.setAttribute("p2Name", p2Name);
                session.setAttribute("role", "p2");
                //给p1发送自己的名字p2name
                JSONObject jo = new JSONObject();
                jo.put("msgType", "0");
                jo.put("p2Name", p2Name);
                WebSocketServer.sendInfo(jo.toJSONString(), p1Name);
                return "room/room";
            } else return "/room/roomIsFull";
        }
    }


}
