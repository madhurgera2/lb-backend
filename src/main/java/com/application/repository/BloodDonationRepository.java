package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.application.model.BloodDonation;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long>, JpaSpecificationExecutor<BloodDonation> {
}