package com.application.dto;

import com.application.model.BloodRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequestDTO {
    private Long id;
    private Double units;
    private String bloodGroup;
    private String patientName;
    private LocalDateTime requireBefore;
    private String status;
    private Double approvedUnits;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
    private UserProfileDTO doctor;

    // Getters and setters
    // ... (generate these)

    public BloodRequestDTO(BloodRequest bloodRequest) {
        this.id = bloodRequest.getId();
        this.units = bloodRequest.getUnits();
        this.bloodGroup = bloodRequest.getBloodGroup();
        this.patientName = bloodRequest.getPatientName();
        this.requireBefore = bloodRequest.getRequireBefore();
        this.approvedUnits = bloodRequest.getApprovedUnits();
        this.status = bloodRequest.getStatus();
        this.createdAt = bloodRequest.getCreatedAt();
        this.user = new UserProfileDTO(bloodRequest.getUser());
        this.doctor = new UserProfileDTO(bloodRequest.getDoctor());
    }

}