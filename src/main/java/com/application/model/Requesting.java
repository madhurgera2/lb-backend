package com.application.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Data
public class Requesting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Blood group is required")
	@Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
	private String bloodGroup;

	@Min(value = 1, message = "At least 1 unit of blood is required")
	private int units;

	private String disease;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String mobile;

	@NotBlank(message = "Gender is required")
	private String gender;

	@Min(value = 18, message = "Age must be at least 18")
	private int age;

	@NotBlank(message = "Status is required")
	private String status;

	@ManyToOne
	@JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, foreignKey = @ForeignKey(name = "FK_requesting_user"))
	@JsonBackReference("user-request")
	private User user;

	@OneToMany(mappedBy = "requesting", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JsonManagedReference("user-request")
	private List<Donor> donors;
	
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	public Requesting() {
		super();
	}

	public Requesting(int id, String name, String email, String bloodGroup, int units, String disease, String mobile,
			String gender, int age, String status) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.bloodGroup = bloodGroup;
		this.units = units;
		this.disease = disease;
		this.mobile = mobile;
		this.gender = gender;
		this.age = age;
		this.status = status;
	}
}
