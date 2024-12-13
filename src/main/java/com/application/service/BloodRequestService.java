package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.model.BloodRequest;
import com.application.model.User;
import com.application.repository.BloodRequestRepository;
import com.application.repository.UserRepository;

import java.util.List;

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

    public List<BloodRequest> getBloodRequestsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Find blood requests by user
        return bloodRequestRepository.findByUserId(userId);
    }
}

