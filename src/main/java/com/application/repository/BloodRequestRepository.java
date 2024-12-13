package com.application.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.model.BloodRequest;
import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    @Query("SELECT br FROM BloodRequest br WHERE br.user.id = :userId")
    List<BloodRequest> findByUserId(@Param("userId") Long userId);
}
