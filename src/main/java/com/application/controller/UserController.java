package com.application.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.constants.Role;
import com.application.custom_excs.InvalidCredentialsException;
import com.application.custom_excs.UserAlreadyExistsException;
import com.application.custom_excs.UserNotFoundException;
import com.application.model.AuthRequest;
import com.application.model.JwtResponse;
import com.application.model.User;
import com.application.service.UserService;
import com.application.util.JwtUtils;

import com.application.dto.UserProfileDTO;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticate(@RequestBody AuthRequest authRequest) {
		// Simplified approach
		User user = userService.fetchUserByEmail(authRequest.getEmail());
		if (user == null) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword()));

		String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
		return ResponseEntity.ok(token);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody User user) {
		if (user.getEmail() == null || user.getPassword() == null) {
			throw new IllegalArgumentException("Email and password are required.");
		}

		User authenticatedUser = userService.fetchUserByEmail(user.getEmail());
		if (authenticatedUser == null
				|| !passwordEncoder.matches(user.getPassword(), authenticatedUser.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		String token = jwtUtil.generateToken(authenticatedUser.getEmail(), authenticatedUser.getRole());
		return ResponseEntity.ok(new JwtResponse(token, authenticatedUser.getEmail()));
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
		if (user.getEmail() == null || user.getEmail().isEmpty()) {
			throw new IllegalArgumentException("Email is required for registration!");
		}

		if (userService.fetchUserByEmail(user.getEmail()) != null) {
			throw new UserAlreadyExistsException("User with email already exists!");
		}

		if (user.getPassword() == null || user.getPassword().isEmpty()) {
			throw new IllegalArgumentException("Password is required!");
		}

		if (user.getPassword().length() < 6) {
			throw new IllegalArgumentException("Password must be at least 6 characters long!");
		}

		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPassword);

		if (user.getRole() != null && !user.getRole().isEmpty()) {
			Role role = Role.fromString(user.getRole());
			user.setRole(role.getRole());
		} else {
			user.setRole(Role.USER.getRole());
		}

		User savedUser = userService.saveUser(user);
		return ResponseEntity.ok(savedUser);
	}

	@PutMapping("/update/{email}")
	public ResponseEntity<?> updateUserProfile(@PathVariable String email, @RequestBody User user) {
		User updatedUser = userService.updateUserProfile(email, user);
		if (updatedUser == null) {
			throw new UserNotFoundException("User not found with email: " + email);
		}
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/delete/{email}")
	public ResponseEntity<String> deleteUser(@PathVariable String email) {
		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found");
		}

		userService.deleteUserByEmail(email);
		return ResponseEntity.ok("User deleted successfully");
	}

	@GetMapping("/userlist")
	public ResponseEntity<?> getUsers() {
		List<User> users = userService.getAllUsers();
		if (users.isEmpty()) {
			throw new UserNotFoundException("No users found");
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/profileDetails/{email}")
	public ResponseEntity<User> getProfileDetails(@PathVariable String email) {
		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found with email: " + email);
		}
		return ResponseEntity.ok(user);
	}

	@GetMapping("/getTotalUsers")
	public ResponseEntity<Integer> getTotalUsers() {
		int totalUsers = userService.getAllUsers().size();
		return ResponseEntity.ok(totalUsers);
	}

	@GetMapping("/currentUser")
	public ResponseEntity<?> getCurrentUserProfile() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}

		User currentUser = userService.fetchUserByEmail(username);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		return ResponseEntity.ok(new UserProfileDTO(currentUser));
	}

	@GetMapping("/listDoctors")
	public ResponseEntity<?> getDoctors() {
		List<User> doctors = userService.getDoctors();
		if (doctors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No doctors found");
		}

		List<UserProfileDTO> doctorProfiles = doctors.stream()
			.map(UserProfileDTO::new)
			.collect(Collectors.toList());

		return ResponseEntity.ok(doctorProfiles);
	}
}