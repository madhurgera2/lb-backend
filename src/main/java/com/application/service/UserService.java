package com.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.model.User;
import com.application.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				new ArrayList<>());
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User updateUserProfile(String email, User user) {
		User existingUser = userRepository.findByEmail(email);

		if (existingUser != null) {
			existingUser.setUsername(user.getUsername());
			existingUser.setMobile(user.getMobile());
			existingUser.setBloodgroup(user.getBloodgroup());
			existingUser.setGender(user.getGender());
			existingUser.setAge(user.getAge());

			// Check if the password is updated. If so, hash it before saving.
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			} else {
				// If no new password is provided, retain the existing one
				existingUser.setPassword(existingUser.getPassword());
			}

			// Only update the role if it's provided (optional)
			if (user.getRole() != null && !user.getRole().isEmpty()) {
				existingUser.setRole(user.getRole());
			}

			return userRepository.save(existingUser);

		} else {
			return null;
		}
	}

	public void deleteUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			userRepository.delete(user);
		}
	}

	public User fetchUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User fetchUserByEmailAndPassword(String email, String password) {
		return userRepository.findByEmailAndPassword(email, password);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<User> fetchProfileByEmail(String email) {
		return userRepository.findProfileByEmail(email);
	}

}