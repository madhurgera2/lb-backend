package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.dto.BloodRequestDTO;
import com.application.dto.UserProfileDTO;
import com.application.model.BloodRequest;
import com.application.model.User;
import com.application.repository.BloodRequestRepository;
import com.application.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

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
