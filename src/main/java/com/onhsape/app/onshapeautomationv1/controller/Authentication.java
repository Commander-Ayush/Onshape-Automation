package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;
import com.onhsape.app.onshapeautomationv1.entity.GraphicsUser;
import com.onhsape.app.onshapeautomationv1.repository.UserRepository;
import com.onhsape.app.onshapeautomationv1.service.FailedOrderService;
import com.onhsape.app.onshapeautomationv1.service.FailedOrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Authentication {

    private final UserRepository userRepository;
    private final FailedOrderService failedOrderService;
    private final PasswordEncoder passwordEncoder;

    public Authentication(UserRepository userRepository,
                          FailedOrderServiceImpl failedOrderService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.failedOrderService = failedOrderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${test.emailId}")
    private String testEmail;

    @Value("${test.password}")
    private String testPassword;

    @Value("${MASTERS_EMAIL}")
    private String mastersEmail;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("demoEmail", testEmail);
        model.addAttribute("demoPassword", testPassword);
        return "login";
    }

    // Authentication.java - updated authenticate() method
    @PostMapping("/login-form")
    @ResponseBody
    public ResponseEntity<String> authenticate(@RequestBody GraphicsUser graphicsUser,
                                               HttpServletRequest request, HttpSession session) {

        String email = graphicsUser.getEmailAccount();
        String password = graphicsUser.getPassword();

        if(email.equals(testEmail) && password.equals(testPassword)){
            GraphicsUser user = userRepository.findByEmailAccount(email)
                    .orElseGet(() -> {
                        GraphicsUser newUser = new GraphicsUser();
                        newUser.setEmailAccount(email);
                        newUser.setPassword(passwordEncoder.encode(password));
                        newUser.setRole(GraphicsUser.Role.USER);
                        return userRepository.save(newUser);
                    });

            return authenticateAndRespond(user, request, session);
        }

        //for admin

        if(email.equals(mastersEmail)){
            GraphicsUser user = userRepository.findByEmailAccount(email)
                    .orElseGet(() -> {
                        GraphicsUser newUser = new GraphicsUser();
                        newUser.setEmailAccount(email);
                        newUser.setPassword(passwordEncoder.encode(password));
                        newUser.setRole(GraphicsUser.Role.ADMIN);
                        return userRepository.save(newUser);
                    });

            return authenticateAndRespond(user, request, session);
        }

        //For real/actual users authentication starts from here

        if (!isValidUser(email, password)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Find existing user or create new one with default USER role
        GraphicsUser user = userRepository.findByEmailAccount(email)
                .orElseGet(() -> {
                    GraphicsUser newUser = new GraphicsUser();
                    newUser.setEmailAccount(email);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setRole(GraphicsUser.Role.USER);
                    return userRepository.save(newUser);
                });

        return authenticateAndRespond(user, request, session);
    }

    /**
     * Builds the Spring Security auth context from a DB user, stores it in the session,
     * and returns the role-based redirect. Shared by all three login branches above.
     */
    private ResponseEntity<String> authenticateAndRespond(GraphicsUser user,
                                                          HttpServletRequest request,
                                                          HttpSession session) {
        String authority = "ROLE_" + user.getRole().name(); // "ROLE_USER" or "ROLE_ADMIN"

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user.getEmailAccount(),
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        session.setAttribute("user", user);

        String redirectUrl = user.getRole() == GraphicsUser.Role.ADMIN ? "/admin" : "/home";
        return ResponseEntity.ok(redirectUrl);
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