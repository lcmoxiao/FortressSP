package com.banmo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class RoomController {


    //只能从RoomList跳转进来
    @RequestMapping("/room")
    public String showRoom() {

        return "room/room";
    }


}
