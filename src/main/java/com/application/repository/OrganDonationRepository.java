package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.model.OrganDonation;
import java.util.List;

@Repository
public interface OrganDonationRepository extends 
    JpaRepository<OrganDonation, Long>, 
    JpaSpecificationExecutor<OrganDonation> {
    
    @Query("SELECT od FROM OrganDonation od WHERE od.user.id = :userId")
    List<OrganDonation> findByUserId(@Param("userId") Long userId);
}