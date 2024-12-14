package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.dto.BloodRequestDTO;
import com.application.dto.UserProfileDTO;
import com.application.model.BloodBank;
import com.application.model.BloodRequest;
import com.application.model.User;
import com.application.repository.BloodBankRepository;
import com.application.repository.BloodRequestRepository;
import com.application.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private BloodBankRepository bloodBankRepository;
    
    @Autowired
    private UserRepository userRepository;

    public BloodRequest createBloodRequest(BloodRequest bloodRequest, Long userId, Long doctorId) {
        // Fetch user
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Fetch doctor
        User doctor = userRepository.findById(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        bloodRequest.setUser(user);
        bloodRequest.setDoctor(doctor);
        return bloodRequestRepository.save(bloodRequest);
    }

    @Transactional
    public BloodRequestDTO approveBloodRequest(
        Long bloodRequestId, 
        Long adminId, 
        Double approvedUnits
    ) {
        // Fetch the blood request
        BloodRequest bloodRequest = bloodRequestRepository.findById(bloodRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Blood Request not found"));

        // Validate request status
        if ("APPROVED".equals(bloodRequest.getStatus()) || 
            "REJECTED".equals(bloodRequest.getStatus())) {
            throw new IllegalStateException("Blood Request cannot be processed as it is already " + bloodRequest.getStatus());
        }

        // Fetch admin user
        User admin = userRepository.findById(adminId);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }

        // Find the oldest usable blood bank entries for the requested blood group
        List<BloodBank> oldestBloodBankEntries = bloodBankRepository.findOldestUsableBloodByGroup(
            bloodRequest.getBloodGroup()
        );

        // Check if enough units are available
        double totalAvailableUnits = oldestBloodBankEntries.stream()
            .mapToDouble(BloodBank::getApprovedUnits)
            .sum();

        if (totalAvailableUnits < approvedUnits) {
            throw new IllegalStateException("Insufficient blood units available for the requested blood group");
        }

        // Update blood bank entries to USED status
        double remainingUnitsToUse = approvedUnits;
        for (BloodBank bloodBankEntry : oldestBloodBankEntries) {
            if (remainingUnitsToUse <= 0) break;

            double unitsToUse = Math.min(bloodBankEntry.getApprovedUnits(), remainingUnitsToUse);
            bloodBankEntry.setStatus("USED");
            bloodBankEntry.setApprovedUnits(bloodBankEntry.getApprovedUnits() - unitsToUse);
            bloodBankRepository.save(bloodBankEntry);

            remainingUnitsToUse -= unitsToUse;
        }

        // Update blood request status
        bloodRequest.setStatus("ALLOTTED");
        bloodRequest.setApprovedUnits(approvedUnits);
        bloodRequest.setProcessedBy(admin);
        bloodRequest.setProcessedAt(LocalDateTime.now());

        // Save the updated blood request
        BloodRequest updatedRequest = bloodRequestRepository.save(bloodRequest);

        // Convert and return the updated request
        return new BloodRequestDTO(updatedRequest);
    }

    public List<BloodRequestDTO> getBloodRequestsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Find blood requests by user and map to DTO
        return bloodRequestRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

    }

    private BloodRequestDTO convertToDTO(BloodRequest bloodRequest) {
        BloodRequestDTO dto = new BloodRequestDTO();
        dto.setId(bloodRequest.getId());
        dto.setUnits(bloodRequest.getUnits());
        dto.setBloodGroup(bloodRequest.getBloodGroup());
        dto.setPatientName(bloodRequest.getPatientName());
        dto.setApprovedUnits(bloodRequest.getApprovedUnits());
        dto.setRequireBefore(bloodRequest.getRequireBefore());
        dto.setStatus(bloodRequest.getStatus());
        dto.setCreatedAt(bloodRequest.getCreatedAt());

        // Add user information
        dto.setUser(new UserProfileDTO(
            bloodRequest.getUser()));

        // Add doctor information
        dto.setDoctor(new UserProfileDTO(
            bloodRequest.getDoctor()));

        return dto;
    }
}

