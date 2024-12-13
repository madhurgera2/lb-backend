package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.model.FundDonation;

import java.util.List;

@Repository
public interface FundDonationRepository extends JpaRepository<FundDonation, Long> {
    @Query("SELECT fd FROM FundDonation fd WHERE fd.user.id = :userId")
    List<FundDonation> findByUserId(@Param("userId") Long userId);
}