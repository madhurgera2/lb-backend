package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.model.BloodBank;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
}