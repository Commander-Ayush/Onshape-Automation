package com.onhsape.app.onshapeautomationv1.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutomationExecutioner {

    public ResponseEntity<String> createAutomation(String scriptFileName, String emailAccount, String password) {

        Map<String, String> body = new HashMap<>();
        body.put("email", emailAccount);
        body.put("password", password);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:3000/" + scriptFileName,
                body,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Automation executed successfully");
        }

        return ResponseEntity.status(response.getStatusCode())
                .body("Automation returned non-200");
    }
}
