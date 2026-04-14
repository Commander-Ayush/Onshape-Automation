package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.entity.FailedOrder;
import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.repository.FailedOrdersRepo;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.onhsape.app.onshapeautomationv1.service.AssignmentService;
import com.onhsape.app.onshapeautomationv1.service.FailedOrderService;
import com.onhsape.app.onshapeautomationv1.service.ImageService;
import com.onhsape.app.onshapeautomationv1.service.ReferralServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Value("${file.upload.location}")
    private String scriptUploadDirectory;

    private final AssignmentService assignmentService;
    private final ReferralServiceImpl referralServiceImpl;
    private final OrderRepository orderRepository;
    private final ImageService imageService;
    private final FailedOrderService failedOrderService;
    private final FailedOrdersRepo failedOrdersRepo;

    private final List<Map<String, String>> errorLog = new ArrayList<>();

    public AdminController(OrderRepository orderRepository,
                           ImageService imageService,
                           AssignmentService assignmentService,
                           ReferralServiceImpl referralServiceImpl,
                           FailedOrderService failedOrderService,
                           FailedOrdersRepo failedOrdersRepo) {
        this.orderRepository = orderRepository;
        this.imageService = imageService;
        this.assignmentService = assignmentService;
        this.referralServiceImpl = referralServiceImpl;
        this.failedOrderService = failedOrderService;
        this.failedOrdersRepo =failedOrdersRepo;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Integer totalEarning = orderRepository.getTotalEarnings();
        model.addAttribute("TotalEarning", totalEarning);
        return "admin";
    }

    @PostMapping("/admin/assignment-upload")
    @ResponseBody
    public ResponseEntity<String> assignmentUpload(
            @RequestParam("image") MultipartFile file,
            @RequestParam("nameOfImage") String imageName,
            @RequestParam("scriptFile") MultipartFile scriptFile,
            @RequestParam("nameOfAssignment") String name,
            @RequestParam("dimensionOfAssignment") String dimension,
            @RequestParam("collegeOfAssignment") String collegeName,
            @RequestParam("branchOfAssignment") String branch,
            @RequestParam("priceOfAssignment") Integer price) {

        try {
            Assignment assignment = new Assignment();
            assignment.setNameOfAssignment(name);
            assignment.setDimensionOfAssignment(dimension);
            assignment.setCollegeOfAssignment(collegeName);
            assignment.setBranchOfAssignment(branch);
            assignment.setPriceOfAssignment(price);
            assignment.setImageName(imageName);

            File dir = new File(scriptUploadDirectory);
            if (!dir.exists()) dir.mkdirs();

            String fileName = scriptFile.getOriginalFilename();
            File targetFile = new File(scriptUploadDirectory + fileName);

            if (targetFile.exists()) {
                boolean deleted = targetFile.delete();
                if (!deleted) return new ResponseEntity<>("Failed to replace existing file", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            scriptFile.transferTo(targetFile);
            assignment.setScriptFileName(fileName);

            Map data = this.imageService.upload(file);
            assignment.setImageURL((String) data.get("url"));

            assignmentService.saveAssignment(assignment);
            return new ResponseEntity<>("success", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/admin/failed-orders")
    public ResponseEntity<List<FailedOrder>> getFailedOrders() {
        return ResponseEntity.ok(failedOrderService.getAllFailedOrders());
    }

    @DeleteMapping("/admin/failed-orders/{id}")
    public ResponseEntity<?> deleteFailedOrder(@PathVariable Integer id) {
        FailedOrder order = failedOrdersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        failedOrderService.deleteFailedOrders(order);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/failed-orders")
    public ResponseEntity<?> clearAllFailedOrders() {
        failedOrdersRepo.deleteAll();
        return ResponseEntity.ok().build();
    }
}