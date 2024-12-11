package com.application.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.application.model.Donor;
import com.application.model.User;

public interface DonorRepository extends JpaRepository<Donor, Integer> {

	@Query(value = "select * from donor", nativeQuery = true)
	public List<Donor> findBloodDetails();

	@Transactional
	@Modifying
	@Query(value = "delete from donor where name = ?1", nativeQuery = true)
	public void deleteByUsername(String name);

	List<Donor> findByBloodGroup(String bloodGroup);

	List<Donor> findByCity(String city);

	List<Donor> findByGender(String gender);

	List<Donor> findByBloodGroupAndCity(String bloodGroup, String city);

	List<Donor> findByAgeBetween(int minAge, int maxAge);

	List<Donor> findByUser(User user);

	Page<Donor> findByBloodGroup(String bloodGroup, Pageable pageable);
}
