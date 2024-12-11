package com.application.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.constants.Role;
import com.application.custom_excs.InvalidTokenException;
import com.application.custom_excs.UserNotFoundException;
import com.application.model.Event;
import com.application.model.User;
import com.application.model.Volunteer;
import com.application.service.EventService;
import com.application.service.SendGridEmailService;
import com.application.service.UserService;
import com.application.service.VolunteerService;
import com.application.util.JwtUtils;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

	@Autowired
	private VolunteerService volunteerService;

	@Autowired
	private EventService eventService;

	@Autowired
	private SendGridEmailService sendGridEmailService;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(VolunteerController.class);

	@PostMapping("/register")
	public ResponseEntity<String> registerVolunteer(@Valid @RequestBody Volunteer volunteer,
			HttpServletRequest request) {

		String token = extractTokenFromRequest(request);
		String email = jwtUtils.extractUsername(token);

		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found for email: " + email);
		}

		volunteer.setUser(user);

		if (volunteer.getRole() != null && !volunteer.getRole().isEmpty()) {
			Role role = Role.fromString(volunteer.getRole());
			volunteer.setRole(role.getRole());
		} else {
			volunteer.setRole(Role.VOLUNTEER.getRole());
		}

		if (volunteer.getEvent() != null) {
			Event event = eventService.getEventById(volunteer.getEvent().getId());
			volunteer.setEvent(event);
		}

		volunteerService.saveVolunteer(volunteer);

		return ResponseEntity.status(HttpStatus.CREATED).body("Volunteer registration successful.");
	}

	@PostMapping("/sg/register")
	public ResponseEntity<String> registerAsVolunteer(@RequestBody Volunteer volunteer) {
		try {

			Volunteer savedVolunteer = volunteerService.saveVolunteer(volunteer);

			String subject = "Volunteer Registration Confirmation";
			String body = String.format(
					"Dear %s,%n%nThank you for registering as a volunteer. We appreciate your support!",
					savedVolunteer.getFirstName());

			String emailResponse = sendGridEmailService.sendEmail(savedVolunteer.getEmail(), subject, body);

			if (emailResponse.contains("Error")) {
				return ResponseEntity.ok("Volunteer registered successfully, but failed to send confirmation email.");
			}

			return ResponseEntity.ok("Volunteer registered successfully. A confirmation email has been sent.");
		} catch (Exception e) {
			return ResponseEntity.status(500)
					.body("An error occurred while registering the volunteer. Please try again later.");
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<Volunteer>> getAllVolunteers() {
		List<Volunteer> volunteers = volunteerService.getAllVolunteers();
		return ResponseEntity.ok(volunteers);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Volunteer> getVolunteerById(@PathVariable Long id) {
		try {
			Volunteer volunteer = volunteerService.getVolunteerById(id);
			return ResponseEntity.ok(volunteer);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // You could customize this to return an
																			// error message.
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateVolunteer(@PathVariable Long id, @RequestBody Volunteer volunteerDetails) {
		try {
			volunteerService.updateVolunteer(id, volunteerDetails);
			return ResponseEntity.ok("Volunteer updated successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("An error occurred while updating the volunteer. Please try again.");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteVolunteer(@PathVariable Long id) {
		try {
			volunteerService.deleteVolunteer(id);
			return ResponseEntity.ok("Volunteer deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Volunteer not found or could not be deleted.");
		}
	}

	@GetMapping("/status/{id}")
	public ResponseEntity<String> checkVolunteerStatus(@PathVariable Long id) {
		Volunteer volunteer = volunteerService.getVolunteerById(id);
		if (volunteer != null) {
			return ResponseEntity.ok("Volunteer is registered.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Volunteer not found.");
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<Volunteer>> searchVolunteersByName(@RequestParam String firstName) {
		List<Volunteer> volunteers = volunteerService.searchVolunteersByFirstName(firstName);
		return ResponseEntity.ok(volunteers);
	}

	private String extractTokenFromRequest(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
			logger.warn("Authorization header is missing or empty");
			throw new InvalidTokenException("Authorization token is required but missing.");
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
			logger.warn("Authorization header does not start with 'Bearer ': {}", authorizationHeader);
			throw new InvalidTokenException("Authorization token is invalid.");
		}

		String token = authorizationHeader.substring(7).trim();
		if (token.isEmpty()) {
			logger.warn("Token extracted from Authorization header is empty");
			throw new InvalidTokenException("Authorization token is invalid or empty.");
		}

		return token;
	}

}
