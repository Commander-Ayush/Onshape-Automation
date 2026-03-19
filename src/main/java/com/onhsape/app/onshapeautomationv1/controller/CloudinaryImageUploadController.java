package com.onhsape.app.onshapeautomationv1.controller;

import com.cloudinary.Cloudinary;
import com.onhsape.app.onshapeautomationv1.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("admin/cloudinary/upload")
public class CloudinaryImageUploadController {

    @Autowired
    private ImageService imageService;

    @PostMapping
    public ResponseEntity<Map> uploadImage(@RequestParam("image")MultipartFile file){
        Map data = this.imageService.upload(file);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
