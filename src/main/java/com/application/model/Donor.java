package com.application.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Donor {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotBlank(message = "Blood group is required")
	@Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
	@Column(name = "bloodgroup")
	private String bloodGroup;

	@NotNull(message = "Units are required")
	@Positive(message = "Units must be a positive number")
	private int units;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String mobile;

	@NotBlank(message = "Gender is required")
	private String gender;

	@NotNull(message = "Age is required")
	@Positive(message = "Age must be a positive number")
	private int age;

	@NotBlank(message = "City is required")
	private String city;

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "Date is required")
	private String date;

	@ManyToOne
	@JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, foreignKey = @ForeignKey(name = "FK_donor_user"))
	@JsonBackReference("user-donor")
	private User user;

	@ManyToOne(optional = true)
	@JoinColumn(name = "request_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_donor_request"))
	@JsonBackReference("user-request")
	private Requesting requesting;

	@ManyToOne
	@JoinColumn(name = "event_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_donor_event"))
	@JsonBackReference("event-donor")
	private Event event;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	public Donor(int id, String name, String bloodGroup, int units, String mobile, String gender, int age, String city,
			String address, String date) {
		super();
		this.id = id;
		this.name = name;
		this.bloodGroup = bloodGroup;
		this.units = units;
		this.mobile = mobile;
		this.gender = gender;
		this.age = age;
		this.city = city;
		this.address = address;
		this.date = date;
	}
}
