package com.application.model;

import lombok.Data;

@Data
public class AuthRequest {
	private String email;
	private String password;

	private String role;

	public AuthRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

}