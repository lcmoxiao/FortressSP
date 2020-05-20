package com.banmo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/room")
public class RoomController {

    @RequestMapping("/show")
    public String showRoom() {

        return "room/room";
    }

    @GetMapping("/page")
    public ModelAndView page() {
        return new ModelAndView("/room/websocket");
    }




}
