package com.banmo.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @PostMapping("/loginSuccess")
    public String loginSuccess() {
        System.out.println("my loginSuccess");
        return "/protected/loginSuccess";
    }

    @PostMapping("/logoutSuccess")
    public String logoutSuccess() {
        System.out.println("my logoutSuccess");
        return "logoutSuccess";
    }

    @PostMapping("/loginError")
    public String loginError() {
        System.out.println("my loginError");
        return "/public/login_error";
    }

    @PostMapping("/logoutPage")
    public String logoutPage() {
        System.out.println("my logoutPage");
        return "/protected/logoutPage";
    }


}
