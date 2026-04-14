package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;

import com.onhsape.app.onshapeautomationv1.model.PaymentVerification;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorPay implements PayService {

    private final OrderRepository orderRepository;


    public RazorPay(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Override
    public AssignmentOrder createOrder(AssignmentOrder assignmentOrder) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey,apiSecret);
        JSONObject orderObject = new JSONObject();
        orderObject.put("amount", assignmentOrder.getPrice()*100);
        orderObject.put("currency", "INR");
        orderObject.put("receipt", assignmentOrder.getAssignmentName());

        Order razorPayOrder = razorpayClient.orders.create(orderObject);

        if(razorPayOrder != null){
            assignmentOrder.setStatus(razorPayOrder.get("status"));
            assignmentOrder.setRazorpayOrderId(razorPayOrder.get("id"));
        }
        return assignmentOrder;
    }

    @Override
    public boolean verifyPayment(PaymentVerification verification) throws RazorpayException {

        // Razorpay expects the signature to be verified against:
        // razorpay_order_id + "|" + razorpay_payment_id
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id",   verification.getRazorpayOrderId());
        options.put("razorpay_payment_id", verification.getRazorpayPaymentId());
        options.put("razorpay_signature",  verification.getRazorpaySignature());

        return Utils.verifyPaymentSignature(options, apiSecret);
    }

    @Override
    public AssignmentOrder saveOrder(AssignmentOrder order) {
        order.setStatus("pending");
        return orderRepository.save(order);
    }
}
