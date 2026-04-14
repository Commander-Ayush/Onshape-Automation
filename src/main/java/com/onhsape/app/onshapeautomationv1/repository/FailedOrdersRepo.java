package com.onhsape.app.onshapeautomationv1.repository;

import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedOrdersRepo extends JpaRepository<FailedOrder,Integer> {

    FailedOrder findByCustomerEmail(String customerEmail);
}
