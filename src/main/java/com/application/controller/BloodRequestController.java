package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.dto.BloodRequestDTO;
import com.application.model.BloodRequest;
import com.application.service.BloodRequestService;
import com.application.util.CurrentUserUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/blood-request")
public class BloodRequestController {
    @Autowired
    private BloodRequestService bloodRequestService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createBloodRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            // Create BloodRequest object from request body
            BloodRequest bloodRequest = new BloodRequest();
            bloodRequest.setUnits(Double.valueOf(requestBody.get("units").toString()));
            bloodRequest.setBloodGroup(requestBody.get("blood_group").toString());
            bloodRequest.setPatientName(requestBody.get("patient_name").toString());
            
            // Parse require before time
            String requireBeforeStr = requestBody.get("require_before").toString();
            bloodRequest.setRequireBefore(LocalDateTime.parse(requireBeforeStr));

            // Extract user and doctor IDs
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            Long doctorId = Long.valueOf(requestBody.get("doctor_id").toString());

            BloodRequest savedRequest = bloodRequestService.createBloodRequest(bloodRequest, userId, doctorId);
            return ResponseEntity.ok(savedRequest);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listBloodRequests(
        @RequestParam(required = false) Long userId
    ) {
        try {
            if (userId != null) {
                List<BloodRequestDTO> requests = bloodRequestService.getBloodRequestsByUserId(userId);
                return ResponseEntity.ok(requests);
            } else {
                // If no user_id is provided, you might want to return all requests or return an error
                return ResponseEntity.badRequest().body("User ID is required");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approve/{bloodRequestId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> approveBloodRequest(
        @PathVariable Long bloodRequestId,
        @RequestBody Map<String, Object> requestBody
    ) {
        try {
            // Get current admin ID
            Long adminId = currentUserUtil.getCurrentUserId();

            // Extract approved units from request body
            Double approvedUnits = Double.valueOf(requestBody.get("approvedUnits").toString());

            // Approve blood request
            BloodRequestDTO approvedRequest = bloodRequestService.approveBloodRequest(
                bloodRequestId, 
                adminId, 
                approvedUnits
            );

            return ResponseEntity.ok(approvedRequest);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}