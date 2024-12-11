package com.application.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.model.ContactUs;
import com.application.model.User;
import com.application.service.CaptchaService;
import com.application.service.ContactUsService;
import com.application.service.UserDetailsServiceImpl;
import com.application.util.JwtUtils;

@RestController
public class PublicController {

	@Autowired
	private JwtUtils jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private CaptchaService captchaService;

	@Autowired
	private ContactUsService contactUsService;

	private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

	@GetMapping("/")
	public String welcomeMessage() {
		return "Welcome to Blood Bank Management system !!!";
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {

		if (user.getEmail() == null || user.getPassword() == null) {
			throw new IllegalArgumentException("Email and password are required.");
		}

		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
			String token = jwtUtil.generateToken(userDetails.getUsername());
			return new ResponseEntity<>(token, HttpStatus.OK);
		} catch (BadCredentialsException e) {
			logger.warn("Invalid credentials for email: {}", user.getEmail());
			return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			logger.error("Unexpected error during login", e);
			return new ResponseEntity<>("An error occurred during login", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/contact-us")
	public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactUs contactUs,
			@RequestParam String captchaToken) {
		if (!captchaService.verifyCaptcha(captchaToken)) {
			return ResponseEntity.badRequest().body("CAPTCHA validation failed");
		}

		contactUsService.saveContactUs(contactUs);
		return ResponseEntity.ok("Contact form submitted successfully!");
	}

}
