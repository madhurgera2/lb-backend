package com.application.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dob;

	@NotBlank(message = "Gender is required")
	private String gender;
	
	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String phone;

	@Email(message = "Invalid email address")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "Street address is required")
	private String address;

	@NotBlank(message = "City is required")
	private String city;

	@NotBlank(message = "State is required")
	private String state;

	@NotBlank(message = "ZIP code is required")
	private String zipCode;

	@NotBlank(message = "Emergency contact name is required")
	private String emergencyContactName;

	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid emergency contact phone number")
	private String emergencyContactPhone;
	
	private String role;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> volunteerRole;

	private Boolean consentContact;

	private Boolean consentDataProcessing;

	@ManyToOne
	@JoinColumn(name = "user_email", foreignKey = @ForeignKey(name = "FK_user"))
	@JsonBackReference("user-volunteer")
	private User user;

	@ManyToOne(optional = true)
	@JoinColumn(name = "event_id", foreignKey = @ForeignKey(name = "FK_event"))
	@JsonBackReference("event-volunteer")
	private Event event;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

}
