package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.model.OrganRequest;
import java.util.List;

@Repository
public interface OrganRequestRepository extends 
    JpaRepository<OrganRequest, Long>, 
    JpaSpecificationExecutor<OrganRequest> {
    
    @Query("SELECT org FROM OrganRequest org WHERE org.user.id = :userId")
    List<OrganRequest> findByUserId(@Param("userId") Long userId);
}