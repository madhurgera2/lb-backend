package com.application.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.application.model.Requesting;
import com.application.model.User;

public interface RequestingBloodRepository extends JpaRepository<Requesting, Integer> {

	public List<Requesting> findByEmail(String email);

	@Transactional
	@Modifying
	@Query(value = "update requesting set status = 'accept' where email = ?1", nativeQuery = true)
	public void updateStatus(String email);

	@Transactional
	@Modifying
	@Query(value = "update requesting set status = 'reject' where email = ?1", nativeQuery = true)
	public void rejectStatus(String email);

	List<Requesting> findByBloodGroup(String bloodGroup);

	List<Requesting> findByStatus(String status);

	List<Requesting> findByUser(User user);

	Page<Requesting> findByEmail(String email, Pageable pageable);

	List<Requesting> findByStatus(String status, Sort sort);
}
