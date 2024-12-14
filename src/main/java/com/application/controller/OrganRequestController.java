package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.application.dto.OrganRequestDTO;
import com.application.model.OrganRequest;
import com.application.service.OrganRequestService;
import com.application.util.CurrentUserUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organ-request")
public class OrganRequestController {
    @Autowired
    private OrganRequestService organRequestService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @PostMapping("/create")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createOrganRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract request details
            String organType = requestBody.get("organType").toString();
            Double units = Double.parseDouble(requestBody.get("units").toString());
            String patientName = requestBody.get("patientName").toString();

            // Optional doctor ID
            Long doctorId = requestBody.containsKey("doctorId") 
                ? Long.parseLong(requestBody.get("doctorId").toString()) 
                : null;

            // Parse require before date if provided
            LocalDateTime requireBefore = null;
            if (requestBody.containsKey("requireBefore")) {
                try {
                    requireBefore = LocalDateTime.parse(requestBody.get("requireBefore").toString());
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Invalid date format for requireBefore");
                }
            }

            // Get current user ID
            Long userId = currentUserUtil.getCurrentUserId();

            // Create OrganRequest object
            OrganRequest organRequest = new OrganRequest();
            organRequest.setOrganType(organType);
            organRequest.setUnits(units);
            organRequest.setPatientName(patientName);
            organRequest.setRequireBefore(requireBefore);

            // Create organ request
            OrganRequestDTO createdRequest = organRequestService.createOrganRequest(
                organRequest, 
                userId, 
                doctorId
            );

            return ResponseEntity.ok(createdRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOrganRequests(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String organType,
        @RequestParam(required = false) String status
    ) {
        try {
            List<OrganRequestDTO> requests = organRequestService.listOrganRequests(
                userId, organType, status
            );
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approve/{organRequestId}")
    @PreAuthorize("hasRole('dean')")
    public ResponseEntity<?> approveOrganRequest(
        @PathVariable Long organRequestId,
        @RequestBody Map<String, Object> requestBody
    ) {
        try {
            // Get current dean ID
            Long deanId = currentUserUtil.getCurrentUserId();

            // Extract approved units from request body
            Double approvedUnits = Double.valueOf(requestBody.get("approvedUnits").toString());

            // Approve organ request
            OrganRequestDTO approvedRequest = organRequestService.approveOrganRequest(
                organRequestId, 
                deanId, 
                approvedUnits
            );

            return ResponseEntity.ok(approvedRequest);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{organRequestId}")
    @PreAuthorize("hasRole('dean')")
    public ResponseEntity<?> rejectOrganRequest(
        @PathVariable Long organRequestId
    ) {
        try {
            // Get current dean ID
            Long deanId = currentUserUtil.getCurrentUserId();

            // Reject organ request
            OrganRequestDTO rejectedRequest = organRequestService.rejectOrganRequest(
                organRequestId, 
                deanId
            );

            return ResponseEntity.ok(rejectedRequest);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}