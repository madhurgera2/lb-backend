package com.application.service;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.model.BloodDonation;
import com.application.model.User;
import com.application.repository.BloodDonationRepository;
import com.application.repository.UserRepository;

@Service
public class BloodDonationService {
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
}