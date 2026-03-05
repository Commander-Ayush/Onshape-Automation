package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.repository.AssignmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AssignmentServiceImpl implements AssignmentService{

    @Autowired
    private AssignmentRepo assignmentRepo;


    @Override
    public List<Assignment> getAllAssignments() {
        return assignmentRepo.findAll();
    }
}
