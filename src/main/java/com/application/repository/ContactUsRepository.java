package com.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.application.model.ContactUs;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

	@Query("SELECT c FROM ContactUs c ORDER BY c.id DESC")
	List<ContactUs> findRecentSubmissions();

	List<ContactUs> findByEmail(String email);

}
