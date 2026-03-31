package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.repository.ReferralRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReferralServiceImpl implements ReferralService{

    private final ReferralRepository referralRepo;

    public ReferralServiceImpl(ReferralRepository referralRepo) {
        this.referralRepo = referralRepo;
    }

    @Override
    public void saveReferral(Referral referral){
        referralRepo.save(referral);
    }

    @Override
    public Optional<Referral> checkReferralCode(String referralCode){
        return referralRepo.findByReferralCode(referralCode);
    }

    @Override
    public Optional<Referral> checkRazorpayorderId(String razorpayOrderId) {
        return referralRepo.findByRazorpayOrderId(razorpayOrderId);
    }


}
