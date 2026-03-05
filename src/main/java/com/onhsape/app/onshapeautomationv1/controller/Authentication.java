package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import com.onhsape.app.onshapeautomationv1.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Authentication {

    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login-form")
    @ResponseBody
    public ResponseEntity<String> authenticate(@RequestBody GraphicsUser graphicsUser, HttpServletRequest request){

        String emailAccount = graphicsUser.getEmailAccount();
        String password = graphicsUser.getPassword();

        if (isValidUser(emailAccount, password)) {

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            emailAccount,
                            null,
                            List.of(new SimpleGrantedAuthority("USER"))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            userRepository.save(graphicsUser);
            return ResponseEntity.ok("Success");
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    private boolean isValidUser(String emailAccount, String password) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> body = new HashMap<>();
            body.put("username", emailAccount);
            body.put("password", password);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:3000/login",
                    body,
                    Map.class
            );

            return (Boolean) response.getBody().get("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

