package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {

	public Admin findByEmail(String email);

	public Admin findByUsername(String username);
}
