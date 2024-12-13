package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.model.FundRequest;

import java.util.List;

@Repository
public interface FundRequestRepository extends JpaRepository<FundRequest, Long> {
    @Query("SELECT fr FROM FundRequest fr WHERE fr.user.id = :userId")
    List<FundRequest> findByUserId(@Param("userId") Long userId);
}