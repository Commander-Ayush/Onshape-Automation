package com.onhsape.app.onshapeautomationv1.repository;

import com.onhsape.app.onshapeautomationv1.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral,Long> {

    Optional<Referral> findByReferralCode(String referralCode);

    Optional<Referral> findByRazorpayOrderId(String razorpayOrderId);
}
