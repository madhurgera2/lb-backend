package com.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.application.dto.BloodDonationDTO;
import com.application.model.BloodBank;
import com.application.model.BloodDonation;
import com.application.model.BloodDonation;
import com.application.model.User;
import com.application.repository.BloodBankRepository;
import com.application.repository.BloodDonationRepository;
import com.application.repository.UserRepository;
import com.application.specification.BloodDonationSpecification;

@Service
public class BloodDonationService {
    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private BloodDonationRepository bloodDonationRepository;

    @Autowired
    private UserRepository userRepository;

    public BloodDonation createBloodDonation(BloodDonation bloodDonation, Long userId) {
        // Validate and fetch user
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        bloodDonation.setUser(user);
        return bloodDonationRepository.save(bloodDonation);
    }

    public List<BloodBank> approveBloodDonation(Long bloodDonationId, Long adminId, Double approvedUnits) {
        // Fetch blood donation
    BloodDonation bloodDonation = bloodDonationRepository.findById(bloodDonationId)
        .orElseThrow(() -> new IllegalArgumentException("Blood Donation not found"));

    // Fetch admin
    User admin = userRepository.findById(adminId);
    if (admin == null) {
        throw new IllegalArgumentException("Admin not found");
    }

    // Create a list to store blood bank entries
    List<BloodBank> bloodBankEntries = new ArrayList<>();

    // Create individual entries for each unit
    for (int i = 0; i < approvedUnits; i++) {
        BloodBank bloodBankEntry = new BloodBank();
        bloodBankEntry.setBloodDonation(bloodDonation);
        bloodBankEntry.setAdmin(admin);
        bloodBankEntry.setStatus("COMPLETED");
        bloodBankEntry.setApprovedUnits(1.0); // Each entry is a single unit
        bloodBankEntry.setApprovedAt(LocalDate.now());
        
        bloodBankEntries.add(bloodBankRepository.save(bloodBankEntry));
    }

    // Update blood donation status
    bloodDonation.setStatus("COMPLETED");
    bloodDonationRepository.save(bloodDonation);

    return bloodBankEntries;
    }

    @Transactional
    public List<BloodBank> rejectBloodDonation(
        Long bloodDonationId, 
        Long adminId, 
        String rejectionReason
    ) {
        // Fetch the blood donation
        BloodDonation bloodDonation = bloodDonationRepository.findById(bloodDonationId)
            .orElseThrow(() -> new IllegalArgumentException("Blood Donation not found"));

        // Verify the donation is in a state that can be rejected
        if ("REJECTED".equals(bloodDonation.getStatus()) || 
            "APPROVED".equals(bloodDonation.getStatus())) {
            throw new IllegalStateException("Blood Donation cannot be rejected as it is already " + bloodDonation.getStatus());
        }

        // Update donation status to REJECTED
        bloodDonation.setStatus("REJECTED");

        // Save the updated donation
        bloodDonationRepository.save(bloodDonation);

        // Return any associated blood bank entries (if any)
        return bloodBankRepository.findByBloodDonation(bloodDonation);
    }

    @Transactional(readOnly = true)
    public List<BloodDonationDTO> listBloodDonations(
        Long userId, 
        String status, 
        String bloodGroup
    ) {
        // Create specification based on filters
        Specification<BloodDonation> spec = BloodDonationSpecification.filterBloodDonations(
            userId, status, bloodGroup
        );

        // Find blood requests matching the specification and map to DTO
        return bloodDonationRepository.findAll(spec).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private BloodDonationDTO convertToDTO(BloodDonation bloodDonation) {
        BloodDonationDTO dto = new BloodDonationDTO(bloodDonation);
        return dto;
    }
}