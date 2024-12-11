package com.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.model.Volunteer;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

	List<Volunteer> findByFirstNameContainingIgnoreCase(String firstName);
}