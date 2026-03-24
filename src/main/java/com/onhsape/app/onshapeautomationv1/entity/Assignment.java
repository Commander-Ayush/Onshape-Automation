package com.onhsape.app.onshapeautomationv1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String nameOfAssignment;

    private String dimensionOfAssignment;

    private String collegeOfAssignment;

    private String branchOfAssignment;

    private Integer priceOfAssignment;

    // I have removed automationName;

    private String imageURL;

    private String imageName;

    private String scriptFileName;


}
