package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.dto.FundRequestDTO;
import com.application.dto.UserProfileDTO;
import com.application.model.FundRequest;
import com.application.model.User;
import com.application.repository.FundRequestRepository;
import com.application.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundRequestService {
    @Autowired
    private FundRequestRepository fundRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public FundRequest createFundRequest(FundRequest fundRequest, Long userId, Long doctorId) {
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

        fundRequest.setUser(user);
        fundRequest.setDoctor(doctor);
        return fundRequestRepository.save(fundRequest);
    }

    public List<FundRequestDTO> getFundRequestsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Find fund requests by user
        return fundRequestRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private FundRequestDTO convertToDTO(FundRequest fundRequest) {
        FundRequestDTO dto = new FundRequestDTO();
        dto.setId(fundRequest.getId());
        dto.setAmount(fundRequest.getAmount());
        dto.setPatientName(fundRequest.getPatientName());
        dto.setCreatedAt(fundRequest.getCreatedAt());

        // Add user information
        dto.setUser(new UserProfileDTO(
            fundRequest.getUser()));

        // Add doctor information
        dto.setDoctor(new UserProfileDTO(
            fundRequest.getDoctor()));

        return dto;
    }
}