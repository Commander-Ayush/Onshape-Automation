package com.onhsape.app.onshapeautomationv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class Authentication {

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
