package com.application.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "admins")
@Data
public class Admin {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Id
	private String email;

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	private String password;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String mobile;

	@NotBlank(message = "Gender is required")
	private String gender;

	@Min(value = 18, message = "Age must be at least 18")
	private int age;

	private String role;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	public Admin() {
		super();
	}

	public Admin(String email, String username, String password, String role) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}

}
