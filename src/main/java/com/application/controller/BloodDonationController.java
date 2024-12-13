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

import com.application.model.BloodDonation;
import com.application.service.BloodDonationService;

@RestController
@RequestMapping("/api/blood-donation")
public class BloodDonationController {
    @Autowired
    private BloodDonationService bloodDonationService;

    @PostMapping("/donate")
    public ResponseEntity<?> donateBlood(@Valid @RequestBody Map<String, Object> requestBody) {
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
}