package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;

public interface MailService {

    String sendConfirmationMail(String userEmail, String referralCode);

    String faliureNotificationMail(String userEmail, FailedOrder failedOrder);
}
