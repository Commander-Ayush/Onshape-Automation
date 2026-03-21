package com.onhsape.app.onshapeautomationv1.controller;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.repository.AssignmentRepo;
import com.onhsape.app.onshapeautomationv1.repository.OrderRepository;
import com.onhsape.app.onshapeautomationv1.service.AssignmentServiceImpl;
import com.onhsape.app.onshapeautomationv1.service.ImageService;

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

    private final AssignmentRepo assignmentRepo;
    @Value("${file.upload.location}")
    private String scriptUploadDirectory;

    private final AssignmentServiceImpl assignmentServiceImpl;

    private OrderRepository orderRepository;

    private ImageService imageService;

    public AdminController(AssignmentServiceImpl assignmentServiceImpl, OrderRepository orderRepository, ImageService imageService, AssignmentRepo assignmentRepo) {
        this.orderRepository = orderRepository;
        this.imageService = imageService;
        this.assignmentServiceImpl = assignmentServiceImpl;
        this.assignmentRepo = assignmentRepo;
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

            //saving the file in Puppeteer Automations

            File dir = new File(scriptUploadDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = scriptUploadDirectory + scriptFile.getOriginalFilename();
            scriptFile.transferTo(new File(filePath));
            assignment.setScriptFileName(scriptFile.getOriginalFilename());

            //Saving the Image on Cloudinary
            Map data = this.imageService.upload(file);
            String imageURL = (String) data.get("url");
            String imageName = (String) data.get("name");
            assignment.setImageURL(imageURL);
            assignment.setImageName(imageName);

            //Saving the assignment to the database.
            assignmentServiceImpl.saveAssignment(assignment);

            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }
}
