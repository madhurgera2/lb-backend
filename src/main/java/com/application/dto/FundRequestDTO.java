package com.application.dto;

import com.application.model.FundRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundRequestDTO {
    private Long id;
    private Double amount;
    private String patientName;
    private String status;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
    private UserProfileDTO doctor;

    // Getters and setters
    // ... (generate these)

    public FundRequestDTO(FundRequest fundRequest) {
        this.id = fundRequest.getId();
        this.amount = fundRequest.getAmount();
        this.patientName = fundRequest.getPatientName();
        this.status = fundRequest.getStatus();
        this.createdAt = fundRequest.getCreatedAt();
        this.user = new UserProfileDTO(fundRequest.getUser());
        this.doctor = new UserProfileDTO(fundRequest.getDoctor());
    }
}