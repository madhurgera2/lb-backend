package com.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.model.ContactUs;
import com.application.repository.ContactUsRepository;

@Service
public class ContactUsService {

	@Autowired
	private ContactUsRepository contactUsRepository;

	public ContactUs saveContactUs(ContactUs contactUs) {
		return contactUsRepository.save(contactUs);
	}

	public Optional<ContactUs> findContactFormById(Long id) {
		return contactUsRepository.findById(id);
	}

	public List<ContactUs> getAllContactForms() {
		return contactUsRepository.findAll();
	}

	public List<ContactUs> findByEmail(String email) {
		return contactUsRepository.findByEmail(email);
	}

	public void deleteContactFormById(Long id) {
		contactUsRepository.deleteById(id);
	}

}
