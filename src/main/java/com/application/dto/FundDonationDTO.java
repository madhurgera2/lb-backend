package com.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FundDonationDTO {
    private Long id;
    private Long fundRequestId;
    private String patientName;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    private UserProfileDTO user;
}