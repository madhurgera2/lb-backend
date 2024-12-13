package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.dto.FundRequestDTO;
import com.application.model.FundRequest;
import com.application.service.FundRequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fund-request")
public class FundRequestController {
    @Autowired
    private FundRequestService fundRequestService;

    @PostMapping("/create")
    public ResponseEntity<?> createFundRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            // Create FundRequest object from request body
            FundRequest fundRequest = new FundRequest();
            fundRequest.setAmount(Double.valueOf(requestBody.get("amount").toString()));
            fundRequest.setPatientName(requestBody.get("patient_name").toString());
            
            // Optional description
            if (requestBody.containsKey("description")) {
                fundRequest.setDescription(requestBody.get("description").toString());
            }

            // Extract user and doctor IDs
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            Long doctorId = Long.valueOf(requestBody.get("doctor_id").toString());

            FundRequest savedRequest = fundRequestService.createFundRequest(fundRequest, userId, doctorId);
            return ResponseEntity.ok(savedRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFundRequests(
        @RequestParam(required = false) Long userId
    ) {
        try {
            if (userId != null) {
                List<FundRequestDTO> requests = fundRequestService.getFundRequestsByUserId(userId);
                return ResponseEntity.ok(requests);
            } else {
                // If no user_id is provided, you might want to return all requests or return an error
                return ResponseEntity.badRequest().body("User ID is required");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}