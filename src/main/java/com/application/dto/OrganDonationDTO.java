package com.application.dto;

import com.application.model.OrganDonation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class OrganDonationDTO {
    private Long id;
    private String organType;
    private Double units;
    private String status;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
    private LocalDate dateOfDonation;
    private LocalTime timeOfDonation;

    public OrganDonationDTO() {}

    public OrganDonationDTO(OrganDonation organDonation) {
        this.id = organDonation.getId();
        this.organType = organDonation.getOrganType();
        this.units = organDonation.getUnits();
        this.status = organDonation.getStatus();
        this.createdAt = organDonation.getCreatedAt();
        this.user = new UserProfileDTO(organDonation.getUser());
        this.dateOfDonation = organDonation.getDateOfDonation();
        this.timeOfDonation = organDonation.getTimeOfDonation();
    }
}