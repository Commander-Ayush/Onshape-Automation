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
        AssignmentOrder createdOrder = payService.createOrder(order);
        System.out.println("Order created: " + createdOrder.toString());
        return ResponseEntity.ok(createdOrder);
        //this createdOrder contains name, scriptName, price, referralCodeUsedByCustomerForThisOrder, user_sEmailId
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
    public ResponseEntity<?> saveOrderExecuteAutomationAndVerify(@RequestBody Map<String, Object> body, HttpSession session){

        AssignmentOrder order = new AssignmentOrder();
        GraphicsUser user = (GraphicsUser) session.getAttribute("user");

        order.setAssignmentName((String) body.get("assignmentName"));
        order.setScriptFileName((String) body.get("scriptFileName"));
        order.setPrice((Integer)  body.get("price"));
        order.setReferralCodeUsed((String) body.get("referralCode"));
        order.setRazorpayPaymentId((String) body.get("razorpayPaymentId"));
        order.setRazorpayOrderId((String) body.get("razorpayOrderId"));
        order.setUserEmail( user.getEmailAccount());

        Referral rCode = userReferralCode.createReferralCode(user.getEmailAccount(), (Integer)body.get("price"));

        order.setUsersGeneratedReferralCode(rCode.getReferralCode());
        rCode.setCommissionMoneyForCustomer(rCode.getDiscount()+5);
        rCode.setRazorpayOrderId((String) body.get("razorpayOrderId"));
        rCode.setUpiId((String) body.get("usersUpiId"));

        referralService.saveReferral(rCode);
        AssignmentOrder savedOrder = payService.saveOrder(order);

        ResponseEntity<Map<String, Object>> automationResponse = (ResponseEntity<Map<String, Object>>) automationExecutioner.createAutomation(
                order.getScriptFileName(),
                user.getEmailAccount(),
                user.getPassword()
        );

        if (automationResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> automationResponseBody = automationResponse.getBody();

            savedOrder.setStatus("completed");
            savedOrder.setNumberOfAttempts((Integer) automationResponseBody.get("numberOfAttempts")); // ✅ correct key
            orderRepository.save(savedOrder);

            mailService.sendConfirmationMail(order.getUserEmail(), order.getUsersGeneratedReferralCode());

            return ResponseEntity.ok(Map.of("status", "ok"));
        }
        else{
            savedOrder.setStatus("failed");
            orderRepository.save(savedOrder);
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", "Automation failed"));

        }
    }

}
