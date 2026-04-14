package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;
import com.onhsape.app.onshapeautomationv1.repository.FailedOrdersRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedOrderServiceImpl implements FailedOrderService{

    private final FailedOrdersRepo  failedOrdersRepo;
    private final MailService mailService;

    @Value("${masters.email}")
    private String masterEmail;

    public FailedOrderServiceImpl(FailedOrdersRepo failedOrdersRepo, MailService mailService) {
        this.failedOrdersRepo = failedOrdersRepo;
        this.mailService = mailService;
    }

    @Override
    public List<FailedOrder> getAllFailedOrders() {
        return  failedOrdersRepo.findAll();
    }

    @Override
    public void saveFailedOrders(FailedOrder failedOrders) {
        mailService.faliureNotificationMail(masterEmail, failedOrders);
        failedOrdersRepo.save(failedOrders);
    }

    @Override
    public void deleteFailedOrders(FailedOrder failedOrder) {
        failedOrdersRepo.delete(failedOrder);
    }

    @Override
    public FailedOrder getFailedOrderByCustomerEmail(String email) {
        return failedOrdersRepo.findByCustomerEmail(email);
    }
}
