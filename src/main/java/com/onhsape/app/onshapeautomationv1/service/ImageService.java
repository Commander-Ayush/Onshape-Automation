package com.onhsape.app.onshapeautomationv1.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageService {

    public Map upload(MultipartFile file);
}
