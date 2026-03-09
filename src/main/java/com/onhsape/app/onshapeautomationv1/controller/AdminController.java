package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.onhsape.app.onshapeautomationv1.service.AssignmentServiceImpl;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


// AdminController.java
@Controller
public class AdminController {

    private final AssignmentServiceImpl assignmentServiceImpl;

    private OrderRepository orderRepository;

    public AdminController(AssignmentServiceImpl assignmentServiceImpl, OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.assignmentServiceImpl = assignmentServiceImpl;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Integer totalEarning = orderRepository.getTotalEarnings();
        model.addAttribute("TotalEarning", totalEarning);
        return "admin";
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
