package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.service.BloodBankService;

@RestController
@RequestMapping("/api/blood-bank")
public class BloodBankController {
    @Autowired
    private BloodBankService bloodBankService;

    @GetMapping("/total-units")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getTotalBloodUnits(
        @RequestParam(required = true) String bloodGroup
    ) {
        try {
            Double totalUnits = bloodBankService.getTotalUsableUnitsByBloodGroup(bloodGroup);
            return ResponseEntity.ok(totalUnits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/blood-group-inventory")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getBloodGroupInventory() {
        try {
            return ResponseEntity.ok(bloodBankService.getBloodGroupInventory());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}