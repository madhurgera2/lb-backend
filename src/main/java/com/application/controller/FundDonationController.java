package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.dto.FundDonationDTO;
import com.application.model.FundDonation;
import com.application.service.FundDonationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fund-donation")
public class FundDonationController {
    @Autowired
    private FundDonationService fundDonationService;

    @PostMapping("/donate")
    public ResponseEntity<?> donateFund(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract parameters
            Long fundRequestId = Long.valueOf(requestBody.get("fund_request_id").toString());
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            Double amount = Double.valueOf(requestBody.get("amount").toString());

            // Create fund donation
            FundDonation savedDonation = fundDonationService.createFundDonation(fundRequestId, userId, amount);
            return ResponseEntity.ok(savedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFundDonations(
        @RequestParam(required = false) Long userId
    ) {
        try {
            if (userId != null) {
                List<FundDonationDTO> donations = fundDonationService.getFundDonationsByUserId(userId);
                return ResponseEntity.ok(donations);
            } else {
                return ResponseEntity.badRequest().body("User ID is required");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}