package com.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.custom_excs.EventNotFoundException;
import com.application.model.Event;
import com.application.repository.EventRepository;

@Service
@Transactional
public class EventService {

	@Autowired
	private EventRepository eventRepository;

	// Create or update an event
	public Event saveEvent(Event event) {
		return eventRepository.save(event);
	}

	// Fetch all events
	public List<Event> getAllEvents() {
		return eventRepository.findAll();
	}

	// Fetch a single event by ID
	public Event getEventById(Long id) {
		Optional<Event> event = eventRepository.findById(id);
		if (event.isPresent()) {
			return event.get();
		}
		throw new EventNotFoundException("Event not found with id: " + id);
	}
	


	// Update an existing event
	public Event updateEvent(Long id, Event updatedEvent) {
		// Check if the event exists
		Optional<Event> existingEventOptional = eventRepository.findById(id);
		if (existingEventOptional.isPresent()) {
			Event existingEvent = existingEventOptional.get();

			// Update fields
			existingEvent.setName(updatedEvent.getName());
			existingEvent.setLocation(updatedEvent.getLocation());
			existingEvent.setDateTime(updatedEvent.getDateTime());
			existingEvent.setOrganizer(updatedEvent.getOrganizer());
			existingEvent.setDescription(updatedEvent.getDescription());

			// Save the updated event
			return eventRepository.save(existingEvent);
		} else {
			throw new RuntimeException("Event not found with id: " + id);
		}
	}

	// Delete an event by ID
	public void deleteEvent(Long id) {
		eventRepository.deleteById(id);
	}
}
