package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.service.RazorPay;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private RazorPay razorPay;

    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount, @RequestParam String currency){

        try{
            return razorPay.createOrder(amount, currency, "r1");
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }

}
