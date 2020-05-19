package com.banmo.demo.controller;

import com.banmo.demo.websocket.WebSocketServer;
import logic.beans.Corps;
import logic.beans.Human;
import logic.core.room.Room;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

import static logic.core.RoomCenter.roomCenter;


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
