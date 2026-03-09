package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;

import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
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
        orderObject.put("receipt", assignmentOrder.getAutomationName());

        Order razorPayOrder = razorpayClient.orders.create(orderObject);

        if(razorPayOrder != null){
            assignmentOrder.setStatus(razorPayOrder.get("status"));
        }

        return orderRepository.save(assignmentOrder);
    }
}
