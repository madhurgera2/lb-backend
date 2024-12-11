package com.application.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RestController;

import com.application.constants.RequestStatus;
import com.application.custom_excs.DonorNotFoundException;
import com.application.custom_excs.InvalidTokenException;
import com.application.custom_excs.ResourceNotFoundException;
import com.application.custom_excs.UserNotFoundException;
import com.application.model.BloodDetails;
import com.application.model.Donor;
import com.application.model.Event;
import com.application.model.Requesting;
import com.application.model.User;
import com.application.service.DonorService;
import com.application.service.EventService;
import com.application.service.UserService;
import com.application.util.JwtUtils;

@RestController
public class DonorController {

	@Autowired
	private DonorService donorService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private EventService eventService;

	@Autowired
	private JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(DonorController.class);

	@PostMapping("/add-donor")
	public ResponseEntity<String> addDonor(@Valid @RequestBody Donor donor, HttpServletRequest request) {

		String token = extractTokenFromRequest(request);
		String email = jwtUtils.extractUsername(token);

		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found for email: " + email);
		}

		donor.setUser(user);
		
		if (donor.getEvent() != null) {
			Event event = eventService.getEventById(donor.getEvent().getId());
			donor.setEvent(event);
		}
		donorService.saveDonor(donor);

		return ResponseEntity.status(HttpStatus.CREATED).body("Donor details added successfully.");
	}

	@GetMapping("/donorlist")
	public ResponseEntity<List<Donor>> getAllDonors() {
		List<Donor> donors = donorService.getAllDonors();
		if (donors.isEmpty()) {
			logger.info("No donors found in the database.");
			return ResponseEntity.noContent().build();
		}
		logger.info("Retrieved {} donors from the database.", donors.size());
		return ResponseEntity.ok(donors);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Donor> getDonorById(@PathVariable int id) {
		Donor donor = donorService.getDonorById(id);
		if (donor == null) {
			throw new DonorNotFoundException("Donor with ID " + id + " not found.");
		}
		return ResponseEntity.ok(donor);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteDonor(@PathVariable int id) {
		// Attempt to delete the donor
		boolean isDeleted = donorService.deleteDonor(id);

		if (!isDeleted) {
			// Donor not found in the system
			throw new DonorNotFoundException("Donor with ID " + id + " not found.");
		}

		// Return a success response
		return ResponseEntity.ok("Donor deleted successfully.");
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

	@GetMapping("/blood-group/{bloodGroup}")
	public ResponseEntity<List<Donor>> getDonorsByBloodGroup(@PathVariable String bloodGroup) {
		// Validate the blood group format if needed
		if (!isValidBloodGroup(bloodGroup)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		List<Donor> donors = donorService.getDonorsByBloodGroup(bloodGroup);

		// Check if donors are found
		if (!donors.isEmpty()) {
			return ResponseEntity.ok(donors);
		} else {
			throw new DonorNotFoundException("No donors found for blood group: " + bloodGroup);
		}
	}

	// Helper method to validate blood group format (example)
	private boolean isValidBloodGroup(String bloodGroup) {
		// Implement your logic to validate blood group (e.g., "A+", "O-", etc.)
		return bloodGroup.matches("^(A|B|AB|O)[+-]$");
	}

	@PostMapping("/blood-requests")
	public ResponseEntity<String> addBloodRequest(@Valid @RequestBody Requesting requesting, HttpServletRequest request) {
		String token = extractTokenFromRequest(request);
		String email = jwtUtils.extractUsername(token);

		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found.");
		}

		requesting.setUser(user);
		requesting.setStatus(RequestStatus.PENDING.getStatus());
		donorService.saveBloodRequest(requesting);

		logger.info("Blood request created successfully for user: {}", email);
		return ResponseEntity.status(HttpStatus.CREATED).body("Blood request created successfully.");
	}

	@GetMapping("/requestHistory")
	public ResponseEntity<List<Requesting>> getRequestHistory() {
		List<Requesting> history = donorService.getRequestHistory();

		if (history.isEmpty()) {
			throw new ResourceNotFoundException("No request history found.");
		}

		return ResponseEntity.ok(history);
	}

	@GetMapping("/requestHistory/{email}")
	public ResponseEntity<List<Requesting>> getRequestHistoryByEmail(@PathVariable String email) {
		List<Requesting> history = donorService.getRequestHistoryByEmail(email);

		if (history.isEmpty()) {
			throw new ResourceNotFoundException("No request history found for email: " + email);
		}

		return ResponseEntity.ok(history);
	}

	@GetMapping("/userRequestHistory")
	public ResponseEntity<List<Requesting>> getRequestHistoryByUser(HttpServletRequest request) {
		String token = extractTokenFromRequest(request);
		String email = jwtUtils.extractUsername(token);
		User user = userService.fetchUserByEmail(email);

		if (user == null) {
			throw new UserNotFoundException("User not found with email: " + email);
		}

		List<Requesting> requestHistory = donorService.getRequestHistoryByUser(user);

		if (requestHistory.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(requestHistory);
		}

		return ResponseEntity.ok(requestHistory);
	}

	@PutMapping("/blood-requests/{id}/approve")
	public ResponseEntity<String> approveRequest(@PathVariable int id) {
		boolean result = donorService.updateRequestStatus(id, RequestStatus.APPROVED.getStatus());

		if (!result) {
			throw new ResourceNotFoundException("Request with ID " + id + " not found.");
		}

		return ResponseEntity.ok("Request approved successfully.");
	}

	@PutMapping("/blood-requests/{id}/reject")
	public ResponseEntity<String> rejectRequest(@PathVariable int id) {
		boolean result = donorService.updateRequestStatus(id, RequestStatus.REJECTED.getStatus());

		if (!result) {
			throw new ResourceNotFoundException("Request with ID " + id + " not found.");
		}

		return ResponseEntity.ok("Request rejected successfully.");
	}

	@GetMapping("/userDonors")
	public ResponseEntity<List<Donor>> getDonorsByUser(HttpServletRequest request) {
		String token = extractTokenFromRequest(request);
		String email = jwtUtils.extractUsername(token);

		if (email == null) {
			throw new InvalidTokenException("Invalid or missing token.");
		}

		User user = userService.fetchUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found for email: " + email);
		}

		List<Donor> donors = donorService.getDonorsByUser(user);
		return ResponseEntity.ok(donors);
	}

	@GetMapping("/requestsByBloodGroup/{bloodGroup}")
	public ResponseEntity<List<Requesting>> getRequestsByBloodGroup(@PathVariable String bloodGroup) {
		List<Requesting> requests = donorService.getRequestsByBloodGroup(bloodGroup);

		if (requests.isEmpty()) {
			throw new ResourceNotFoundException("No requests found for blood group: " + bloodGroup);
		}

		return ResponseEntity.ok(requests);
	}

	@PutMapping("/updateDonor/{id}")
	public ResponseEntity<String> updateDonor(@PathVariable int id, @RequestBody Donor updatedDonor) {
		Donor existingDonor = donorService.getDonorById(id);
		if (existingDonor == null) {
			throw new ResourceNotFoundException("Donor with ID " + id + " not found.");
		}

		existingDonor.setName(updatedDonor.getName());
		existingDonor.setBloodGroup(updatedDonor.getBloodGroup());
		existingDonor.setUnits(updatedDonor.getUnits());

		donorService.saveDonor(existingDonor);
		return ResponseEntity.ok("Donor updated successfully.");
	}

	@GetMapping("/requestsByStatus/{status}")
	public ResponseEntity<List<Requesting>> getRequestsByStatus(@PathVariable String status) {
		List<Requesting> requests = donorService.getRequestsByStatus(status);
		if (requests.isEmpty()) {
			throw new ResourceNotFoundException("No requests found with status: " + status);
		}
		return ResponseEntity.ok(requests);
	}

	@GetMapping("/donorUnitsByBloodGroup")
	public ResponseEntity<List<BloodDetails>> getDonorUnitsByBloodGroup() {
		List<BloodDetails> bloodDetails = donorService.getDonorUnitsByBloodGroup();
		if (bloodDetails.isEmpty()) {
			throw new ResourceNotFoundException("No donor units found by blood group.");
		}
		return ResponseEntity.ok(bloodDetails);
	}

	@GetMapping("/bloodDetails")
	public ResponseEntity<List<BloodDetails>> getBloodDetails() {
		List<Donor> bloodDetails = donorService.getBloodDetails();

		if (bloodDetails.isEmpty()) {
			throw new ResourceNotFoundException("No blood details available.");
		}

		List<Donor> donors = donorService.getAllDonors();
		donorService.checkForOldBloodSamples(donors);

		List<String> groups = new ArrayList<>();
		List<Integer> units = new ArrayList<>();
		Map<String, Integer> details = new LinkedHashMap<>();
		for (Donor donor : bloodDetails) {
			details.put(donor.getBloodGroup(), details.getOrDefault(donor.getBloodGroup(), 0) + 1);

			int index = groups.indexOf(donor.getBloodGroup());
			if (index >= 0) {
				units.set(index, units.get(index) + donor.getUnits());
			} else {
				groups.add(donor.getBloodGroup());
				units.add(donor.getUnits());
			}
		}

		List<BloodDetails> result = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : details.entrySet()) {
			result.add(new BloodDetails(entry.getKey(), entry.getValue(), units.get(0)));
			units.remove(0);
		}

		return ResponseEntity.ok(result);
	}

	@GetMapping("/getTotalDonors")
	public ResponseEntity<List<Integer>> getTotalDonors() {
		List<Donor> donors = donorService.getAllDonors();

		if (donors.isEmpty()) {
			throw new ResourceNotFoundException("No donors found.");
		}

		donorService.checkForOldBloodSamples(donors);

		List<Integer> totalDonors = new ArrayList<>();
		totalDonors.add(donors.size());
		return ResponseEntity.ok(totalDonors);
	}

	@GetMapping("/getTotalBloodGroups")
	public ResponseEntity<List<Integer>> getTotalBloodGroups() {
		List<Donor> bloodDetails = donorService.getBloodDetails();

		if (bloodDetails.isEmpty()) {
			throw new ResourceNotFoundException("No blood details found.");
		}

		Set<String> bloodGroups = new LinkedHashSet<>();
		for (Donor donor : bloodDetails) {
			bloodGroups.add(donor.getBloodGroup());
		}

		List<Integer> totalBloodGroups = new ArrayList<>();
		totalBloodGroups.add(bloodGroups.size());
		return ResponseEntity.ok(totalBloodGroups);
	}

	@GetMapping("/getTotalUnits")
	public ResponseEntity<List<Integer>> getTotalUnits() {
		try {
			List<Donor> bloodDetails = donorService.getBloodDetails();
			int totalUnits = bloodDetails.stream().mapToInt(Donor::getUnits).sum();
			List<Integer> result = new ArrayList<>();
			result.add(totalUnits);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/getTotalRequests/{email}")
	public ResponseEntity<List<Integer>> getTotalRequests(@PathVariable String email) {
		try {
			List<Requesting> requestHistory = donorService.getRequestHistoryByEmail(email);
			List<Integer> result = new ArrayList<>();
			result.add(requestHistory.size());
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/getTotalDonationCount/{email}")
	public ResponseEntity<List<Integer>> getTotalDonationCount(@PathVariable String email) {
		try {
			// Get all Donor records
			List<Donor> donors = donorService.getAllDonors();

			// Count the donations by this specific user
			long donationCount = donors.stream()
					.filter(donor -> donor.getUser() != null && donor.getUser().getEmail().equalsIgnoreCase(email))
					.count();

			List<Integer> result = new ArrayList<>();
			result.add((int) donationCount);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/getTotalDonations")
	public ResponseEntity<List<Integer>> getTotalDonations() {
		try {

			List<Donor> donors = donorService.getAllDonors();

			long donationCount = donors.size();

			List<Integer> result = new ArrayList<>();
			result.add((int) donationCount);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}