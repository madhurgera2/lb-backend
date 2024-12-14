package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.application.dto.OrganDonationDTO;
import com.application.model.OrganDonation;
import com.application.service.OrganDonationService;
import com.application.util.CurrentUserUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organ-donation")
public class OrganDonationController {
    @Autowired
    private OrganDonationService organDonationService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @PostMapping("/create")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createOrganDonation(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract request details
            String organType = requestBody.get("organType").toString();
            Double units = Double.parseDouble(requestBody.get("units").toString());

            // Create OrganDonation object
            OrganDonation organDonation = new OrganDonation();
            organDonation.setOrganType(organType);
            organDonation.setUnits(units);

            // Optional: Set specific donation date and time
            if (requestBody.containsKey("dateOfDonation")) {
                try {
                    organDonation.setDateOfDonation(
                        LocalDate.parse(requestBody.get("dateOfDonation").toString())
                    );
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Invalid date format for dateOfDonation");
                }
            }

            if (requestBody.containsKey("timeOfDonation")) {
                try {
                    organDonation.setTimeOfDonation(
                        LocalTime.parse(requestBody.get("timeOfDonation").toString())
                    );
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Invalid time format for timeOfDonation");
                }
            }

            // Get current user ID
            Long userId = currentUserUtil.getCurrentUserId();

            // Create organ donation
            OrganDonationDTO createdDonation = organDonationService.createOrganDonation(
                organDonation, 
                userId
            );

            return ResponseEntity.ok(createdDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOrganDonations(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String organType,
        @RequestParam(required = false) String status
    ) {
        try {
            List<OrganDonationDTO> donations = organDonationService.listOrganDonations(
                userId, organType, status
            );
            return ResponseEntity.ok(donations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approve/{organDonationId}")
    @PreAuthorize("hasRole('dean')")
    public ResponseEntity<?> approveOrganDonation(
        @PathVariable Long organDonationId
    ) {
        try {
            // Get current dean ID
            Long deanId = currentUserUtil.getCurrentUserId();

            // Approve organ donation
            OrganDonationDTO approvedDonation = organDonationService.approveOrganDonation(
                organDonationId, 
                deanId
            );

            return ResponseEntity.ok(approvedDonation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{organDonationId}")
    @PreAuthorize("hasRole('dean')")
    public ResponseEntity<?> rejectOrganDonation(
        @PathVariable Long organDonationId
    ) {
        try {
            // Get current dean ID
            Long deanId = currentUserUtil.getCurrentUserId();

            // Reject organ donation
            OrganDonationDTO rejectedDonation = organDonationService.rejectOrganDonation(
                organDonationId, 
                deanId
            );

            return ResponseEntity.ok(rejectedDonation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}