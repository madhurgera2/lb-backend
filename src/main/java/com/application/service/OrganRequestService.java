package com.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.application.dto.OrganRequestDTO;
import com.application.model.OrganDonation;
import com.application.model.OrganRequest;
import com.application.model.User;
import com.application.repository.OrganDonationRepository;
import com.application.repository.OrganRequestRepository;
import com.application.repository.UserRepository;
import com.application.specification.OrganRequestSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganRequestService {
    private static final Logger logger = LoggerFactory.getLogger(OrganRequestService.class);

    @Autowired
    private OrganRequestRepository organRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganDonationRepository organDonationRepository;

    @Transactional
    public OrganRequestDTO createOrganRequest(OrganRequest organRequest, Long userId, Long doctorId) {
        try {
            // Fetch user
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Fetch doctor if provided
            User doctor = doctorId != null 
                ? userRepository.findById(doctorId)
                : null;

            // Set relationships
            organRequest.setUser(user);
            organRequest.setDoctor(doctor);

            // Set initial status
            organRequest.setStatus("PENDING");

            // Save organ request
            OrganRequest savedRequest = organRequestRepository.save(organRequest);

            logger.info("Organ Request created. ID: {}, User: {}, Organ Type: {}", 
                savedRequest.getId(), userId, savedRequest.getOrganType());

            return new OrganRequestDTO(savedRequest);
        } catch (Exception e) {
            logger.error("Error creating organ request", e);
            throw new RuntimeException("Failed to create organ request", e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrganRequestDTO> listOrganRequests(
        Long userId,
        String organType,
        String status
    ) {
        // Create specification based on filters
        Specification<OrganRequest> spec = OrganRequestSpecification.filterOrganRequests(
            userId, organType, status
        );

        // Find organ requests matching the specification and map to DTO
        return organRequestRepository.findAll(spec).stream()
            .map(OrganRequestDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrganRequestDTO approveOrganRequest(
        Long organRequestId, 
        Long deanId, 
        Double approvedUnits
    ) {
        try {
            logger.info("Attempting to approve Organ Request. Request ID: {}, Dean ID: {}", 
                organRequestId, deanId);

            // Fetch the organ request
            OrganRequest organRequest = organRequestRepository.findById(organRequestId)
                .orElseThrow(() -> {
                    logger.error("Organ Request not found. Request ID: {}", organRequestId);
                    return new IllegalArgumentException("Organ Request not found");
                });

            // Log current request details
            logger.info("Current Organ Request Details - Status: {}, Units: {}, Organ Type: {}", 
                organRequest.getStatus(), organRequest.getUnits(), organRequest.getOrganType());

            // Validate request status
            if ("APPROVED".equals(organRequest.getStatus()) || 
                "REJECTED".equals(organRequest.getStatus())) {
                logger.warn("Attempt to modify already processed request. Current status: {}", 
                    organRequest.getStatus());
                throw new IllegalStateException("Organ Request cannot be processed as it is already " + organRequest.getStatus());
            }

            // Fetch dean user
            User dean = userRepository.findById(deanId);
            if (dean == null) {
                logger.error("Dean not found. Dean ID: {}", deanId);
                throw new IllegalArgumentException("Dean not found");
            }

            // Validate approved units
            if (approvedUnits > organRequest.getUnits()) {
                logger.warn("Approved units exceed requested units. Requested: {}, Approved: {}", 
                    organRequest.getUnits(), approvedUnits);
                throw new IllegalArgumentException("Approved units cannot exceed requested units");
            }

            // Find the oldest approved organ donation of the same type
            List<OrganDonation> availableDonations = organDonationRepository.findByOrganTypeAndStatusOrderByCreatedAtAsc(
                organRequest.getOrganType(), 
                "APPROVED"
            );

            // Check if enough donations are available
            double totalAvailableDonations = availableDonations.stream()
                .mapToDouble(OrganDonation::getUnits)
                .sum();

            if (totalAvailableDonations < approvedUnits) {
                logger.warn("Insufficient organ donations. Available: {}, Requested: {}", 
                    totalAvailableDonations, approvedUnits);
                throw new IllegalStateException("Not enough organ donations available");
            }

            // Mark donations as used
            double remainingUnitsToUse = approvedUnits;
            for (OrganDonation donation : availableDonations) {
                if (remainingUnitsToUse <= 0) break;

                double unitsToUse = Math.min(donation.getUnits(), remainingUnitsToUse);
                donation.setStatus("USED");
                organDonationRepository.save(donation);

                remainingUnitsToUse -= unitsToUse;
            }

            // Update organ request status
            organRequest.setStatus("APPROVED");
            organRequest.setApprovedUnits(approvedUnits);
            organRequest.setProcessedBy(dean);
            organRequest.setProcessedAt(LocalDateTime.now());

            // Save the updated organ request
            OrganRequest updatedRequest = organRequestRepository.save(organRequest);

            logger.info("Organ Request {} successfully approved by dean {}", 
                organRequestId, deanId);

            // Convert and return the updated request
            return new OrganRequestDTO(updatedRequest);

        } catch (Exception e) {
            // Log the full stack trace for comprehensive debugging
            logger.error("Error approving organ request. Request ID: {}, Dean ID: {}", 
                organRequestId, deanId, e);
            
            // Rollback the transaction manually if needed
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            // Rethrow the exception to be handled by global exception handler
            throw new RuntimeException("Failed to approve organ request: " + e.getMessage(), e);
        }
    }

    @Transactional
    public OrganRequestDTO rejectOrganRequest(
        Long organRequestId, 
        Long deanId
    ) {
        try {
            logger.info("Attempting to reject Organ Request. Request ID: {}, Dean ID: {}", 
                organRequestId, deanId);

            // Fetch the organ request
            OrganRequest organRequest = organRequestRepository.findById(organRequestId)
                .orElseThrow(() -> {
                    logger.error("Organ Request not found. Request ID: {}", organRequestId);
                    return new IllegalArgumentException("Organ Request not found");
                });

            // Log current request details
            logger.info("Current Organ Request Details - Status: {}, Units: {}, Organ Type: {}", 
                organRequest.getStatus(), organRequest.getUnits(), organRequest.getOrganType());

            // Validate request status
            if ("APPROVED".equals(organRequest.getStatus()) || 
                "REJECTED".equals(organRequest.getStatus())) {
                logger.warn("Attempt to modify already processed request. Current status: {}", 
                    organRequest.getStatus());
                throw new IllegalStateException("Organ Request cannot be processed as it is already " + organRequest.getStatus());
            }

            // Fetch dean user
            User dean = userRepository.findById(deanId);
            if (dean == null) {
                logger.error("Dean not found. Dean ID: {}", deanId);
                throw new IllegalArgumentException("Dean not found");
            }

            // Update organ request status
            organRequest.setStatus("REJECTED");
            organRequest.setProcessedBy(dean);
            organRequest.setProcessedAt(LocalDateTime.now());

            // Save the updated organ request
            OrganRequest updatedRequest = organRequestRepository.save(organRequest);

            logger.info("Organ Request {} successfully rejected by dean {}", 
                organRequestId, deanId);

            // Convert and return the updated request
            return new OrganRequestDTO(updatedRequest);

        } catch (Exception e) {
            // Log the full stack trace for comprehensive debugging
            logger.error("Error rejecting organ request. Request ID: {}, Dean ID: {}", 
                organRequestId, deanId, e);
            
            // Rollback the transaction manually if needed
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            // Rethrow the exception to be handled by global exception handler
            throw new RuntimeException("Failed to reject organ request: " + e.getMessage(), e);
        }
    }
}