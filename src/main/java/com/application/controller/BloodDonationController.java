package com.application.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.application.model.BloodBank;
import com.application.model.BloodDonation;
import com.application.service.BloodDonationService;

@RestController
@RequestMapping("/api/blood-donation")
public class BloodDonationController {
    @Autowired
    private BloodDonationService bloodDonationService;

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

    @PostMapping("/approve-donation")
    public ResponseEntity<?> approveBloodDonation(@RequestBody Map<String, Object> requestBody) {
        try {
            Long bloodDonationId = Long.valueOf(requestBody.get("blood_donation_id").toString());
            Long adminId = Long.valueOf(requestBody.get("admin_id").toString());
            Double approvedUnits = Double.valueOf(requestBody.get("approved_units").toString());

            List<BloodBank> approvedDonation = bloodDonationService.approveBloodDonation(bloodDonationId, adminId, approvedUnits);
            return ResponseEntity.ok(approvedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}