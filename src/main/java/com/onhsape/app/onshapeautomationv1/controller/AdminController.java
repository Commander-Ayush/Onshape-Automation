package com.onhsape.app.onshapeautomationv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// AdminController.java
@Controller
public class AdminController {

    @GetMapping("/admin")
    public String admin(Model model) {
        // load admin-specific data here
        return "admin"; // maps to templates/admin.html
    }
}
