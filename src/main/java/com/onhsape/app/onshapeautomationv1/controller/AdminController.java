package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.entity.Referral;
import com.onhsape.app.onshapeautomationv1.repository.AssignmentRepo;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.onhsape.app.onshapeautomationv1.service.AssignmentService;
import com.onhsape.app.onshapeautomationv1.service.AssignmentServiceImpl;
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
import java.util.Map;


// AdminController.java
@Controller
public class AdminController {

    @Value("${file.upload.location}")
    private String scriptUploadDirectory;

    private AssignmentService assignmentService;

    private final ReferralServiceImpl referralServiceImpl;

    private OrderRepository orderRepository;

    private ImageService imageService;

    public AdminController(OrderRepository orderRepository, ImageService imageService, AssignmentService assignmentService, ReferralServiceImpl referralServiceImpl) {
        this.orderRepository = orderRepository;
        this.imageService = imageService;
        this.assignmentService = assignmentService;
        this.referralServiceImpl = referralServiceImpl;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Integer totalEarning = orderRepository.getTotalEarnings();
        model.addAttribute("TotalEarning", totalEarning);
        return "admin";
    }


    @PostMapping("/admin/assignment-upload")
    @ResponseBody
    public ResponseEntity<String> assignmentUpload
            (@RequestParam("image") MultipartFile file,
             @RequestParam("nameOfImage")  String imageName,
             @RequestParam("scriptFile")  MultipartFile scriptFile,
             @RequestParam("nameOfAssignment") String name,
             @RequestParam("dimensionOfAssignment") String dimension,
             @RequestParam("collegeOfAssignment") String collegeName,
             @RequestParam("branchOfAssignment") String branch,
             @RequestParam("priceOfAssignment") Integer price) {

        try{
            Assignment assignment = new Assignment();
            assignment.setNameOfAssignment(name);
            assignment.setDimensionOfAssignment(dimension);
            assignment.setCollegeOfAssignment(collegeName);
            assignment.setBranchOfAssignment(branch);
            assignment.setPriceOfAssignment(price);
            assignment.setImageName(imageName);

            System.out.println("Flow point 1");

            //saving the file in Puppeteer Automations
            File dir = new File(scriptUploadDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            System.out.println("Flow point 2");

            String fileName = scriptFile.getOriginalFilename();
            File targetFile = new File(scriptUploadDirectory + fileName);

            // If file exists → delete it
            if (targetFile.exists()) {
                boolean deleted = targetFile.delete();

                if (!deleted) {
                    return new ResponseEntity<>("Failed to replace existing file", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            System.out.println("Flow point 3");
            // Save new file
            scriptFile.transferTo(targetFile);
            assignment.setScriptFileName(fileName);

            System.out.println("Flow point 4");

            //Saving the Image on Cloudinary
            Map data = this.imageService.upload(file);
            String imageURL = (String) data.get("url");
            assignment.setImageURL(imageURL);

            //Saving the assignment to the database.
            assignmentService.saveAssignment(assignment);

            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("admin/referral-code")
    public ResponseEntity<String> saveReferralCode(@RequestBody Referral referral) {
        try{
            referralServiceImpl.saveReferral(referral);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }
}
