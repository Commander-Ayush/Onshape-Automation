package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.model.PaymentVerification;
import com.onhsape.app.onshapeautomationv1.service.*;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private PayService payService;

    private ReferralService referralService;

    private UserReferralCode userReferralCode;


    public PaymentController(PayService payService, ReferralService referralService, UserReferralCode userReferralCode) {
        this.payService = payService;
        this.referralService = referralService;
        this.userReferralCode = userReferralCode;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody AssignmentOrder order) throws RazorpayException {
        AssignmentOrder savedOrder = payService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/referralCode")
    public ResponseEntity<Map<String, Integer>> checkReferralCode(@RequestBody Map<String, String> body) {

        String enteredReferralCode = body.get("referralCode");
        Optional<Referral> referCode = referralService.checkReferralCode(enteredReferralCode);

        if (referCode.isPresent()) {
            Integer discount = referCode.get().getDiscount();
            return ResponseEntity.ok(Map.of(
                    "valid", 1,
                    "discount", discount
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerification verification) {
        try {
            boolean isValid = payService.verifyPayment(verification);

            if (isValid) {
                return ResponseEntity.ok(Map.of("status", "verified"));


            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "invalid_signature"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/save-order")
    public ResponseEntity<?> saveOrder(@RequestBody AssignmentOrder order, HttpSession session) throws RazorpayException {

        GraphicsUser user = (GraphicsUser) session.getAttribute("user");

        order.setUserEmail(user.getEmailAccount());

        // Gives a unique referral code to every customer
        Referral rCode;
        rCode = userReferralCode.createReferralCode(user.getEmailAccount(), order.getPrice());
        order.setUserReferral(rCode.getReferralCode());

        //Saving the referral code to the DB
        referralService.saveReferral(rCode);

        // referral comes from frontend
        String referralCodeEnteredByUser = order.getReferralCode();
        order.setReferralCode(referralCodeEnteredByUser); {

        return ResponseEntity.ok(payService.saveOrder(order));
    }
    }

}
