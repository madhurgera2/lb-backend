package com.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.custom_excs.UserNotFoundException;
import com.application.model.Volunteer;
import com.application.repository.VolunteerRepository;

@Service
public class VolunteerService {

	@Autowired
	private VolunteerRepository volunteerRepository;

	public Volunteer saveVolunteer(Volunteer volunteer) {
		return volunteerRepository.save(volunteer);
	}

	public List<Volunteer> getAllVolunteers() {
		return volunteerRepository.findAll();
	}

	public Volunteer getVolunteerById(Long id) {
		return volunteerRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Volunteer not found with ID: " + id));
	}

	public Volunteer updateVolunteer(Long id, Volunteer volunteerDetails) {
	    // Fetch existing volunteer from the database
	    Volunteer existingVolunteer = getVolunteerById(id); // Assuming this method fetches the Volunteer by ID

	    // Update the existing volunteer's fields with the details from the incoming volunteerDetails
	    existingVolunteer.setFirstName(volunteerDetails.getFirstName());
	    existingVolunteer.setLastName(volunteerDetails.getLastName());
	    existingVolunteer.setEmail(volunteerDetails.getEmail());
	    existingVolunteer.setPhone(volunteerDetails.getPhone());
	    existingVolunteer.setAddress(volunteerDetails.getAddress());
	    existingVolunteer.setEmergencyContactName(volunteerDetails.getEmergencyContactName());
	    existingVolunteer.setEmergencyContactPhone(volunteerDetails.getEmergencyContactPhone());
	    existingVolunteer.setRole(volunteerDetails.getRole());
	    existingVolunteer.setVolunteerRole(volunteerDetails.getVolunteerRole());
	    existingVolunteer.setConsentContact(volunteerDetails.getConsentContact());
	    existingVolunteer.setConsentDataProcessing(volunteerDetails.getConsentDataProcessing());
	    existingVolunteer.setDob(volunteerDetails.getDob());
	    existingVolunteer.setGender(volunteerDetails.getGender());
	    existingVolunteer.setCity(volunteerDetails.getCity());
	    existingVolunteer.setState(volunteerDetails.getState());
	    existingVolunteer.setZipCode(volunteerDetails.getZipCode());
	    existingVolunteer.setEvent(volunteerDetails.getEvent()); // If event should be updated

	    // Save the updated volunteer object back to the repository
	    return volunteerRepository.save(existingVolunteer);
	}


	
	
	public void deleteVolunteer(Long id) {
		Volunteer existingVolunteer = getVolunteerById(id);
		volunteerRepository.delete(existingVolunteer);
	}
	
	public List<Volunteer> searchVolunteersByFirstName(String firstName) {
	    return volunteerRepository.findByFirstNameContainingIgnoreCase(firstName);
	}

}
