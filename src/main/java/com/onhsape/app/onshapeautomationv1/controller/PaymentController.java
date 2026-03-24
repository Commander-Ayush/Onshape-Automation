package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.model.PaymentVerification;
import com.onhsape.app.onshapeautomationv1.service.PayService;
import com.onhsape.app.onshapeautomationv1.service.RazorPay;
import com.onhsape.app.onshapeautomationv1.service.ReferralService;
import com.onhsape.app.onshapeautomationv1.service.ReferralServiceImpl;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private PayService payService;

    private ReferralService referralService;


    public PaymentController(PayService payService, ReferralService referralService) {
        this.payService = payService;
        this.referralService = referralService;
    }

//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder(@RequestBody AssignmentOrder order)
//            throws RazorpayException {
//        try{
//            AssignmentOrder savedOrder = payService.createOrder(order);
//            String code = user.getEmailAccount().endsWith("@gmail.com") ?
//                    user.getEmailAccount().substring(0, user.getEmailAccount().length()-"@gmail.com".length())
//                    : "";
//            if(order.getPrice()<=45){
//                code.concat("15");
//            } else if (order.getPrice()>=60 && order.getPrice()<=100) {
//                code.concat("20");
//            } else if (order.getPrice()>=300) {
//                code.concat("55");
//            }
//            order.setUserReferral(code);
//            order.setUserName(user.getEmailAccount());
//            return ResponseEntity.ok(savedOrder);
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody AssignmentOrder order,
                                         HttpSession session) throws RazorpayException {

        GraphicsUser user = (GraphicsUser) session.getAttribute("user");

        order.setUserEmail(user.getEmailAccount());

        // referral comes from frontend
        String referralCode = order.getReferralCode();

//        if (referralCode != null) {
//            Optional<Referral> ref = referralService.checkReferralCode(referralCode);
//            ref.ifPresent(r -> {
//                int discount = r.getDiscount();
//                order.setPrice(order.getPrice() - discount);
//            });
//        }

        AssignmentOrder savedOrder = payService.createOrder(order);

        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/referralCode")
    public ResponseEntity<Map<String, Integer>> checkReferralCode(@RequestBody Map<String, String> body) {

        String referralCode = body.get("referralCode");
        Optional<Referral> referCode = referralService.checkReferralCode(referralCode);

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

}
