package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.service.AssignmentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


// AdminController.java
@Controller
public class AdminController {

    @Autowired
    private final AssignmentServiceImpl assignmentServiceImpl;

    public AdminController(AssignmentServiceImpl assignmentServiceImpl) {
        this.assignmentServiceImpl = assignmentServiceImpl;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin"; // maps to templates/admin.html
    }


    @PostMapping("/admin/assignment-upload")
    @ResponseBody
    public ResponseEntity<String> assignmentUpload(@RequestBody Assignment assignment) {

        try{
            assignmentServiceImpl.saveAssignment(assignment);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }
}
