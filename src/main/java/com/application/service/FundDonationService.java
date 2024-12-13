package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.dto.FundDonationDTO;
import com.application.dto.UserProfileDTO;
import com.application.model.FundDonation;
import com.application.model.FundRequest;
import com.application.model.User;
import com.application.repository.FundDonationRepository;
import com.application.repository.FundRequestRepository;
import com.application.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundDonationService {
    @Autowired
    private FundDonationRepository fundDonationRepository;

    @Autowired
    private FundRequestRepository fundRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FundDonation createFundDonation(Long fundRequestId, Long userId, Double amount) {
        // Fetch fund request
        FundRequest fundRequest = fundRequestRepository.findById(fundRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Fund Request not found"));

        // Fetch user
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Create fund donation
        FundDonation fundDonation = new FundDonation();
        fundDonation.setFundRequest(fundRequest);
        fundDonation.setUser(user);
        fundDonation.setAmount(amount);

        return fundDonationRepository.save(fundDonation);
    }

    @Transactional(readOnly = true)
    public List<FundDonationDTO> getFundDonationsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Find fund donations by user and map to DTO
        return fundDonationRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private FundDonationDTO convertToDTO(FundDonation fundDonation) {
        FundDonationDTO dto = new FundDonationDTO();
        dto.setId(fundDonation.getId());
        dto.setFundRequestId(fundDonation.getFundRequest().getId());
        dto.setPatientName(fundDonation.getFundRequest().getPatientName());
        dto.setAmount(fundDonation.getAmount());
        dto.setStatus(fundDonation.getStatus());
        dto.setCreatedAt(fundDonation.getCreatedAt());

        // Add user information
        dto.setUser(new UserProfileDTO(fundDonation.getUser()));

        return dto;
    }
}