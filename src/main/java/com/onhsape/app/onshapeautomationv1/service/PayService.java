package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import com.razorpay.RazorpayException;

public interface PayService {

    AssignmentOrder createOrder(AssignmentOrder orders) throws RazorpayException;
}
