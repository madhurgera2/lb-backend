package com.application.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Event name is required")
	@Size(max = 255, message = "Event name must not exceed 255 characters")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Event location is required")
	@Column(nullable = false)
	private String location;

	@Future(message = "Event date and time must be in the future")
	@Column(nullable = false)
	private LocalDateTime dateTime;

	@NotBlank(message = "Organizer is required")
	@Column(nullable = false)
	private String organizer;

	@Size(max = 1000, message = "Description must not exceed 1000 characters")
	@Column(length = 1000)
	private String description;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("event-volunteer")
	private List<Volunteer> volunteers;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("event-donor")
	private List<Donor> donors;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
}
