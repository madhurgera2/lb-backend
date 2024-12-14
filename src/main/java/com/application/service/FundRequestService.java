package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.application.dto.FundRequestDTO;
import com.application.dto.UserProfileDTO;
import com.application.model.FundRequest;
import com.application.model.User;
import com.application.repository.FundRequestRepository;
import com.application.repository.UserRepository;
import com.application.specification.FundRequestSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundRequestService {
    @Autowired
    private FundRequestRepository fundRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FundRequestDTO> searchFundRequests(
        Long id,
        Long userId, 
        String status, 
        Double minAmount, 
        Double maxAmount
    ) {
        // Create specification based on filters
        Specification<FundRequest> spec = FundRequestSpecification.filterFundRequests(
            id, userId, status, minAmount, maxAmount
        );

        // Find fund requests matching the specification and map to DTO
        return fundRequestRepository.findAll(spec).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public FundRequestDTO approveFundRequest(Long fundRequestId) {
        // Find the fund request
        FundRequest fundRequest = fundRequestRepository.findById(fundRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Fund request not found"));

        // Check if the request is already approved
        if ("APPROVED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already approved");
        }

        if ("REJECTED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already rejected");
        }

        if ("COMPLETED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already completed");
        }

        // Update status to APPROVED
        fundRequest.setStatus("APPROVED");
        FundRequest updatedRequest = fundRequestRepository.save(fundRequest);

        // Convert and return the updated request
        return convertToDTO(updatedRequest);
    }

    @Transactional
    public FundRequestDTO rejectFundRequest(Long fundRequestId) {
        // Find the fund request
        FundRequest fundRequest = fundRequestRepository.findById(fundRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Fund request not found"));

        // Check if the request is already approved
        if ("APPROVED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already approved");
        }

        if ("REJECTED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already rejected");
        }

        if ("COMPLETED".equals(fundRequest.getStatus())) {
            throw new IllegalStateException("Fund request is already completed");
        }

        // Update status to REJECTED
        fundRequest.setStatus("REJECTED");
        FundRequest updatedRequest = fundRequestRepository.save(fundRequest);

        // Convert and return the updated request
        return convertToDTO(updatedRequest);
    }

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
        dto.setDescription(fundRequest.getDescription());
        dto.setStatus(fundRequest.getStatus());
        dto.setCreatedAt(fundRequest.getCreatedAt());

        // Calculate amount raised
        Double amountRaised = fundRequestRepository.calculateTotalDonationsForRequest(fundRequest.getId());
        dto.setAmountRaised(amountRaised);

        // Add user information
        dto.setUser(new UserProfileDTO(
            fundRequest.getUser()));

        // Add doctor information
        dto.setDoctor(new UserProfileDTO(
            fundRequest.getDoctor()));

        return dto;
    }
}