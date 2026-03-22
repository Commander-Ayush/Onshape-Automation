package com.onhsape.app.onshapeautomationv1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;

@Entity
@Getter
public class Referral{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer referralCodeId;

    private String referralCode;

    private Integer discount;

}
