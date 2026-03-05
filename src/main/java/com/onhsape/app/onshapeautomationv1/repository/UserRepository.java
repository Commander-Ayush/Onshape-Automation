package com.onhsape.app.onshapeautomationv1.repository;

import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// UserRepository.java
public interface UserRepository extends JpaRepository<GraphicsUser, Long> {
    Optional<GraphicsUser> findByEmailAccount(String emailAccount);
}