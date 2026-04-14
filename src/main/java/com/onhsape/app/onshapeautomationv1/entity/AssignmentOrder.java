package com.onhsape.app.onshapeautomationv1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignmentName;
    private String scriptFileName;
    private Integer price;
    private String status;
    private String referralCodeUsed;
    private String razorpayOrderId;
    private String usersGeneratedReferralCode;
    private String userEmail;
    private String razorpayPaymentId;
    private Integer commissionMoney;
    private Integer numberOfAttempts;
}
