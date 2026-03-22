package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.Referral;

import java.util.Optional;

public interface ReferralService{

    void saveReferral(Referral referral);

    Optional<Referral> checkReferralCode(String referralCode);


}
