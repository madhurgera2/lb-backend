package com.application.dto;

import com.application.model.OrganRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrganRequestDTO {
    private Long id;
    private String organType;
    private Double units;
    private String status;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
    private UserProfileDTO doctor;
    private String patientName;
    private LocalDateTime requireBefore;
    private Double approvedUnits;

    public OrganRequestDTO() {}

    public OrganRequestDTO(OrganRequest organRequest) {
        this.id = organRequest.getId();
        this.organType = organRequest.getOrganType();
        this.units = organRequest.getUnits();
        this.status = organRequest.getStatus();
        this.createdAt = organRequest.getCreatedAt();
        this.user = new UserProfileDTO(organRequest.getUser());
        this.doctor = organRequest.getDoctor() != null ? new UserProfileDTO(organRequest.getDoctor()) : null;
        this.patientName = organRequest.getPatientName();
        this.requireBefore = organRequest.getRequireBefore();
        this.approvedUnits = organRequest.getApprovedUnits();
    }
}