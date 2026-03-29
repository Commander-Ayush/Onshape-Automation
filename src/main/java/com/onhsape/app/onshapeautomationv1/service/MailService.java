package com.onhsape.app.onshapeautomationv1.service;

public interface MailService {

    String sendConfirmationMail(String userEmail, String referralCode);
}
