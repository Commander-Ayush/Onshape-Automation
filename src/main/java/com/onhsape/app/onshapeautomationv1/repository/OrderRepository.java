package com.onhsape.app.onshapeautomationv1.repository;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<AssignmentOrder,Integer> {

    @Query("SELECT COALESCE(SUM(a.price),0) FROM AssignmentOrder a")
    Integer getTotalEarnings();
}
