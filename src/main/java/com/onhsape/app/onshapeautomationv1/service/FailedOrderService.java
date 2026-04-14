package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;

import java.util.List;

public interface FailedOrderService {

    List<FailedOrder> getAllFailedOrders();

    void saveFailedOrders(FailedOrder failedOrders);

    void deleteFailedOrders(FailedOrder failedOrders);

    FailedOrder getFailedOrderByCustomerEmail(String email);
}
