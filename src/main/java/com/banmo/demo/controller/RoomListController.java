package com.banmo.demo.controller;


import logic.core.RoomCenter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/room/roomList")
public class RoomListController {

    static RoomCenter roomCenter;

    static {
        roomCenter = new RoomCenter();
    }

    @GetMapping("/show")
    public String showRoomList(Model model) {
        model.addAttribute("RoomIds", roomCenter.getRoomIds());
        return "/room/roomList";
    }

    @PostMapping("/add")
    public String createRoom(@RequestParam(name = "roomName") String roomName, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        request.getSession().setAttribute("roomId"
                , roomCenter.createRoom(username, roomName));
        return "redirect:/room";
    }

    @PostMapping("/join")
    public String joinRoom(Model model, @RequestParam(name = "roomId") String roomId,
                           HttpServletRequest request) {
        System.out.println("joinRoom");
        request.getSession().setAttribute("roomId", roomId);
        if (!roomCenter.getRoomIds().contains(roomId)) {
            System.out.println("房间不存在");
            model.addAttribute("RoomIds", roomCenter.getRoomIds());
            model.addAttribute("errorInfo", "房间不存在");
            return "/room/roomList";
        }
        String username = (String) request.getSession().getAttribute("username");
        if (roomCenter.joinRoom(username, roomId)) return "redirect:/room";
        else return "/room/roomIsFull";
    }

}
