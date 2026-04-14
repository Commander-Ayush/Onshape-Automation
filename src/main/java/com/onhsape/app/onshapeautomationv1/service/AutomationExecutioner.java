package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutomationExecutioner {

    private final FailedOrderService  failedOrderService;

    public AutomationExecutioner(FailedOrderService failedOrderService) {
    this.failedOrderService = failedOrderService;}

    public ResponseEntity<Map<String, Object>> createAutomation(String scriptFileName, String emailAccount, String password) {

        System.out.println("Create Automation from AutomationExecutioner has been called for "+ scriptFileName);
        int currentRepetitions = 0;

        Map<String, String> body = new HashMap<>();
        body.put("email", emailAccount);
        body.put("password", password);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response;

        do {
            response = restTemplate.postForEntity(
                    "http://localhost:3000/" + scriptFileName,
                    body,
                    Map.class
            );
            currentRepetitions++;

        } while (currentRepetitions < 4 &&
                !Boolean.TRUE.equals(response.getBody().get("success")));

        if (Boolean.TRUE.equals(response.getBody().get("success"))) {
            return ResponseEntity.ok(Map.of("status","Automation executed successfully", "numberOfAttempts", currentRepetitions));
        }else {
            FailedOrder failedOrders = new FailedOrder();
            failedOrders.setOrderedAutomation(scriptFileName);
            failedOrders.setCustomerEmail(emailAccount);
            failedOrders.setCustomerPass(password);
            failedOrders.setFailureReason("Automation failed");

            failedOrderService.saveFailedOrders(failedOrders);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Automation failed after retries"));
        }
    }
}
