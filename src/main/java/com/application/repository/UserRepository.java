package com.application.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.application.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	User findByEmail(String email);

	User findByUsername(String username);

	User findByEmailAndPassword(String email, String password);

	List<User> findProfileByEmail(String email);

	Page<User> findByEmail(String email, Pageable pageable);

	List<User> findByUsername(String username, Sort sort);
}
