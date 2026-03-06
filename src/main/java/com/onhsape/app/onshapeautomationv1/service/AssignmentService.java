package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.Assignment;
import com.onhsape.app.onshapeautomationv1.repository.AssignmentRepo;

import java.util.List;

public interface AssignmentService {

    List<Assignment> getAllAssignments();

    void saveAssignment(Assignment assignment);



}
