package com.onhsape.app.onshapeautomationv1.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud.name}")
    String cloudName;

    @Value("${cloudinary.cloud.key}")
    String cloudKey;

    @Value("${cloudinary.cloud.secret}")
    String  cloudSecret;


    @Bean
    public Cloudinary cloudinaryConfiguration() {
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", cloudKey);
        config.put("api_secret", cloudSecret);
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
