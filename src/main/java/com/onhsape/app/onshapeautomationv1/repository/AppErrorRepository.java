package com.onhsape.app.onshapeautomationv1.repository;

import com.onhsape.app.onshapeautomationv1.entity.AppError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppErrorRepository extends JpaRepository<AppError, Integer> {

    List<AppError> findAllByOrderByTimestampDesc();
}
