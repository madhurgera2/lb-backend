package com.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.application.dto.OrganDonationDTO;
import com.application.model.OrganDonation;
import com.application.model.User;
import com.application.repository.OrganDonationRepository;
import com.application.repository.UserRepository;
import com.application.specification.OrganDonationSpecification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganDonationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganDonationService.class);

    @Autowired
    private OrganDonationRepository organDonationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrganDonationDTO createOrganDonation(OrganDonation organDonation, Long userId) {
        try {
            // Fetch user
            User user = userRepository.findById(userId);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Set user and initial status
            organDonation.setUser(user);
            organDonation.setStatus("PENDING");

            // Set donation date and time if not provided
            if (organDonation.getDateOfDonation() == null) {
                organDonation.setDateOfDonation(LocalDate.now());
            }
            if (organDonation.getTimeOfDonation() == null) {
                organDonation.setTimeOfDonation(LocalTime.now());
            }

            // Save organ donation
            OrganDonation savedDonation = organDonationRepository.save(organDonation);

            logger.info("Organ Donation created. ID: {}, User: {}, Organ Type: {}", 
                savedDonation.getId(), userId, savedDonation.getOrganType());

            return new OrganDonationDTO(savedDonation);
        } catch (Exception e) {
            logger.error("Error creating organ donation", e);
            throw new RuntimeException("Failed to create organ donation", e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrganDonationDTO> listOrganDonations(
        Long userId,
        String organType,
        String status
    ) {
        // Create specification based on filters
        Specification<OrganDonation> spec = OrganDonationSpecification.filterOrganDonations(
            userId, organType, status
        );

        // Find organ donations matching the specification and map to DTO
        return organDonationRepository.findAll(spec).stream()
            .map(OrganDonationDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrganDonationDTO approveOrganDonation(
        Long organDonationId, 
        Long deanId
    ) {
        try {
            logger.info("Attempting to approve Organ Donation. Donation ID: {}, Dean ID: {}", 
                organDonationId, deanId);

            // Fetch the organ donation
            OrganDonation organDonation = organDonationRepository.findById(organDonationId)
                .orElseThrow(() -> {
                    logger.error("Organ Donation not found. Donation ID: {}", organDonationId);
                    return new IllegalArgumentException("Organ Donation not found");
                });

            // Log current donation details
            logger.info("Current Organ Donation Details - Status: {}, Units: {}, Organ Type: {}", 
                organDonation.getStatus(), organDonation.getUnits(), organDonation.getOrganType());

            // Validate donation status
            if ("APPROVED".equals(organDonation.getStatus()) || 
                "REJECTED".equals(organDonation.getStatus())) {
                logger.warn("Attempt to modify already processed donation. Current status: {}", 
                    organDonation.getStatus());
                throw new IllegalStateException("Organ Donation cannot be processed as it is already " + organDonation.getStatus());
            }

            // Fetch dean user
            User dean = userRepository.findById(deanId);
            if (dean == null) {
                logger.error("Dean not found. Dean ID: {}", deanId);
                throw new IllegalArgumentException("Dean not found");
            }

            // Update organ donation status
            organDonation.setStatus("APPROVED");

            // Save the updated organ donation
            OrganDonation updatedDonation = organDonationRepository.save(organDonation);

            logger.info("Organ Donation {} successfully approved by dean {}", 
                organDonationId, deanId);

            // Convert and return the updated donation
            return new OrganDonationDTO(updatedDonation);

        } catch (Exception e) {
            // Log the full stack trace for comprehensive debugging
            logger.error("Error approving organ donation. Donation ID: {}, Dean ID: {}", 
                organDonationId, deanId, e);
            
            // Rollback the transaction manually if needed
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            // Rethrow the exception to be handled by global exception handler
            throw new RuntimeException("Failed to approve organ donation: " + e.getMessage(), e);
        }
    }

    @Transactional
    public OrganDonationDTO rejectOrganDonation(
        Long organDonationId, 
        Long deanId
    ) {
        try {
            logger.info("Attempting to reject Organ Donation. Donation ID: {}, Dean ID: {}", 
                organDonationId, deanId);

            // Fetch the organ donation
            OrganDonation organDonation = organDonationRepository.findById(organDonationId)
                .orElseThrow(() -> {
                    logger.error("Organ Donation not found. Donation ID: {}", organDonationId);
                    return new IllegalArgumentException("Organ Donation not found");
                });

            // Log current donation details
            logger.info("Current Organ Donation Details - Status: {}, Units: {}, Organ Type: {}", 
                organDonation.getStatus(), organDonation.getUnits(), organDonation.getOrganType());

            // Validate donation status
            if ("APPROVED".equals(organDonation.getStatus()) || 
                "REJECTED".equals(organDonation.getStatus())) {
                logger.warn("Attempt to modify already processed donation. Current status: {}", 
                    organDonation.getStatus());
                throw new IllegalStateException("Organ Donation cannot be processed as it is already " + organDonation.getStatus());
            }

            // Fetch dean user
            User dean = userRepository.findById(deanId);
            if (dean == null) {
                logger.error("Dean not found. Dean ID: {}", deanId);
                throw new IllegalArgumentException("Dean not found");
            }

            // Update organ donation status
            organDonation.setStatus("REJECTED");

            // Save the updated organ donation
            OrganDonation updatedDonation = organDonationRepository.save(organDonation);

            logger.info("Organ Donation {} successfully rejected by dean {}", 
                organDonationId, deanId);

            // Convert and return the updated donation
            return new OrganDonationDTO(updatedDonation);

        } catch (Exception e) {
            // Log the full stack trace for comprehensive debugging
            logger.error("Error rejecting organ donation. Donation ID: {}, Dean ID: {}", 
                organDonationId, deanId, e);
            
            // Rollback the transaction manually if needed
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            // Rethrow the exception to be handled by global exception handler
            throw new RuntimeException("Failed to reject organ donation: " + e.getMessage(), e);
        }
    }
}