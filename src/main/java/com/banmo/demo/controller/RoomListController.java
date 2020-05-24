package com.banmo.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static logic.core.RoomCenter.roomCenter;

@Controller
public class RoomListController {

    @GetMapping("/roomList/show")
    public String showRoomList(Model model) {
        model.addAttribute("RoomInfos", roomCenter.getRoomInfos());
        return "/room/roomList";
    }


}
