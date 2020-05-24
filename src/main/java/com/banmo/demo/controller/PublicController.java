package com.banmo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublicController {

    @RequestMapping("/login")
    public String userLogin() {
        System.out.println("my login");
        return "login";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
