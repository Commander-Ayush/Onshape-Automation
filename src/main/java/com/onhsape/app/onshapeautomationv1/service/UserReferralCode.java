package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.Referral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserReferralCode {

    @Autowired
    private ReferralService referralService;

    private static final String Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom random = new SecureRandom();

    public Referral createReferralCode(String email, Integer price){

        Referral rCode = new Referral();

        email = email.toUpperCase();
        String code = email.substring(0,3);
        Integer discount = 0;

        for(int i = 0; i < 3; i++){
            code = code + Characters.charAt(random.nextInt(Characters.length()));
        }
        if (price <= 45) {
            code += "15";
            discount = 15;
        } else if (price >= 60 && price <= 100) {
            code += "20";
            discount = 20;
        } else if (price >= 300) {
            code += "55";
            discount = 55;
        }

        while (referralService.checkReferralCode(code).isPresent()) {
            createReferralCode(email, price);
        }
        rCode.setReferralCode(code);
        rCode.setDiscount(discount);
        return rCode;
    }
}

