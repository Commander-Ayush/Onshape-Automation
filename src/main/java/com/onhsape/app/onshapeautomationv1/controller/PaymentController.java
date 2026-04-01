package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.model.PaymentVerification;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
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

    private final MailService mailService;
    private final PayService payService;
    private final ReferralService referralService;
    private final UserReferralCode userReferralCode;
    private final OrderRepository  orderRepository;
    private final AutomationExecutioner automationExecutioner;

    public PaymentController(AutomationExecutioner automationExecutioner, PayService payService, ReferralService referralService, UserReferralCode userReferralCode, OrderRepository orderRepository, MailService mailService) {
        this.payService = payService;
        this.referralService = referralService;
        this.orderRepository = orderRepository;
        this.userReferralCode = userReferralCode;
        this.mailService = mailService;
        this.automationExecutioner=automationExecutioner;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody AssignmentOrder order, HttpSession session) throws RazorpayException {
        GraphicsUser user = (GraphicsUser) session.getAttribute("user");
        order.setUserEmail(user.getEmailAccount());
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
    public ResponseEntity<?> saveOrder(@RequestBody AssignmentOrder order, HttpSession session) {

        GraphicsUser user = (GraphicsUser) session.getAttribute("user");

        order.setUserEmail(user.getEmailAccount());

        Referral rCode = userReferralCode.createReferralCode(user.getEmailAccount(), order.getPrice());
        order.setUserReferral(rCode.getReferralCode());
        order.setCommissionMoney(rCode.getDiscount()-5);
        rCode.setRazorpayOrderId(order.getRazorpayOrderId());
        referralService.saveReferral(rCode);

        AssignmentOrder savedOrder = payService.saveOrder(order);

        ResponseEntity<String> automationResponse = automationExecutioner.createAutomation(
                order.getScriptFileName(),
                user.getEmailAccount(),
                user.getPassword()
        );

        if (automationResponse.getStatusCode().is2xxSuccessful()) {
            savedOrder.setStatus("completed");
            orderRepository.save(savedOrder);

            return ResponseEntity.ok(Map.of(
                    "status",          "ok",
                    "userReferral",    rCode.getReferralCode(),
                    "userEmail",       user.getEmailAccount(),
                    "razorpayOrderId", savedOrder.getRazorpayOrderId(),
                    "commissionMoney", rCode.getDiscount() - 5
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Automation failed"));
    }

    @PostMapping("/save-order-status")
    public ResponseEntity<?> savedOrderConfirmation(@RequestBody Map<String, String> body) {

        System.out.println("Process to change the order status initiated");
        String razorpayOrderId = body.get("razorpayOrderId");
        String status = body.get("status");

        AssignmentOrder order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        System.out.println(order.getUserEmail());
        System.out.println(order.getReferralCode());
        orderRepository.save(order);

        mailService.sendConfirmationMail(order.getUserEmail(), order.getUserReferral());


        System.out.println("Change Done order created");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-s-upi")
    public ResponseEntity<?> usersUpi(@RequestBody Referral referral, HttpSession session) {
        System.out.println("user-s-upi has been hit");
        Optional<Referral> ref = referralService.checkRazorpayorderId(referral.getRazorpayOrderId());
        if(ref.isPresent()) {
            System.out.println("Razorpay Order id"+referral.getRazorpayOrderId()+" found");
            Referral customerUPI = ref.get();
            customerUPI.setUpiId(referral.getUpiId());


            referralService.saveReferral(customerUPI);

            return ResponseEntity.ok(Map.of("commission", customerUPI.getDiscount()));
        }
        else System.out.println("Razorpay Order id"+referral.getRazorpayOrderId()+" not found");
        return ResponseEntity.notFound().build();
    }

}
