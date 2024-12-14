package com.application.controller;

import javax.validation.Valid;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.application.dto.BloodDonationDTO;
import com.application.model.BloodBank;
import com.application.model.BloodDonation;
import com.application.service.BloodDonationService;
import com.application.util.CurrentUserUtil;

@RestController
@RequestMapping("/api/blood-donation")
public class BloodDonationController {
    @Autowired
    private BloodDonationService bloodDonationService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @PostMapping("/requestToDonate")
    public ResponseEntity<?> requestToDonateBlood(@Valid @RequestBody Map<String, Object> requestBody) {
        try {

            Long userId = Long.valueOf(requestBody.get("user_id").toString());

            // Create BloodDonation object from request body
            BloodDonation bloodDonation = new BloodDonation();
            bloodDonation.setUnits(Double.valueOf(requestBody.get("units").toString()));
            bloodDonation.setDateOfDonation(LocalDate.parse(requestBody.get("dateOfDonation").toString()));
            bloodDonation.setTimeOfDonation(LocalTime.parse(requestBody.get("timeOfDonation").toString()));

            BloodDonation savedDonation = bloodDonationService.createBloodDonation(bloodDonation, userId);
            return ResponseEntity.ok(savedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> listBloodDonations(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String bloodGroup
    ) {
        try {
            List<BloodDonationDTO> bloodRequests = bloodDonationService.listBloodDonations(userId, status, bloodGroup);
            return ResponseEntity.ok(bloodRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approveDonation/{BloodDonationId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> approveBloodDonation(@RequestBody Map<String, Object> requestBody, @PathVariable Long BloodDonationId) {
        try {
            Long adminId = currentUserUtil.getCurrentUserId();
            Double approvedUnits = Double.valueOf(requestBody.get("approved_units").toString());

            List<BloodBank> approvedDonation = bloodDonationService.approveBloodDonation(BloodDonationId, adminId, approvedUnits);
            return ResponseEntity.ok(approvedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rejectDonation/{BloodDonationId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> rejectBloodDonation(
        @PathVariable Long BloodDonationId, 
        @RequestBody(required = false) Map<String, Object> requestBody
    ) {
        try {
            Long adminId = currentUserUtil.getCurrentUserId();
            
            // Optional: Include a reason for rejection if provided in the request body
            String rejectionReason = requestBody != null && requestBody.containsKey("reason") 
                ? requestBody.get("reason").toString() 
                : "Donation rejected by admin";

            List<BloodBank> rejectedDonation = bloodDonationService.rejectBloodDonation(
                BloodDonationId, 
                adminId, 
                rejectionReason
            );
            return ResponseEntity.ok(rejectedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}