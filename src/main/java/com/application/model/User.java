package com.application.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// @Column(unique = true, nullable = false)
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String mobile;

	@NotBlank(message = "Blood group is required")
	@Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group")
	private String bloodgroup;

	@NotBlank(message = "Gender is required")
	private String gender;

	@Min(value = 18, message = "Age must be at least 18")
	private int age;

	@NotBlank(message = "Password is required")
	private String password;

	private String role;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JsonManagedReference("user-donor")
	private List<Donor> donations;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JsonManagedReference("user-request")
	private List<Requesting> requests;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference("user-volunteer")
	private List<Volunteer> volunteers;
	
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	public User(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

}