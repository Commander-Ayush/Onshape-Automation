package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.AppError;
import com.onhsape.app.onshapeautomationv1.repository.AppErrorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private AppErrorRepository appErrorRepository;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception e, HttpServletRequest request) {

        AppError error = new AppError();
        error.setErrorMessage(e.getMessage());
        error.setEndpoint(request.getRequestURI());
        error.setStackTrace(Arrays.toString(e.getStackTrace()));
        error.setTimestamp(LocalDateTime.now());

        appErrorRepository.save(error);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong"));
    }


}