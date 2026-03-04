package com.onhsape.app.onshapeautomationv1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Controller
public class Authentication {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login-form")
    @ResponseBody
    public ResponseEntity<String> authenticate(@RequestParam String emailAccount, @RequestParam String password, HttpServletRequest request){
        if (isValidUser(emailAccount, password)) {

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            emailAccount,
                            null,
                            List.of(new SimpleGrantedAuthority("USER")));

            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true); // create session
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    private boolean isValidUser(String emailAccount, String password) {
        try {
            ProcessBuilder builder = new ProcessBuilder("node", "login.js", emailAccount, password);

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            boolean success = false;
            while ((line = reader.readLine()) != null) {
                System.out.println("Node Output: " + line);
                if (line.contains("Login Successful")) {
                    success = true;
                }
            }
            process.waitFor();
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

