package com.application.model;

public class JwtResponse {
    private String token;
    private String email;
    
    public JwtResponse(String token) {
        this.token = token;
    }
    
    public JwtResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }
}
