package com.application.dto;

import com.application.model.BloodDonation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonationDTO {
    private Long id;
    private Double units;
    private String status;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
    private LocalDate dateOfDonation;
    private LocalTime timeOfDonation;

    // Getters and setters
    // ... (generate these)

    public BloodDonationDTO(BloodDonation bloodDonation) {
        this.id = bloodDonation.getId();
        this.units = bloodDonation.getUnits();
        this.status = bloodDonation.getStatus();
        this.dateOfDonation = bloodDonation.getDateOfDonation();
        this.timeOfDonation = bloodDonation.getTimeOfDonation();
        this.createdAt = bloodDonation.getCreatedAt();
        this.user = new UserProfileDTO(bloodDonation.getUser());
    }
}