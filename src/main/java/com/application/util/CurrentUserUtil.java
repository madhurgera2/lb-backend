package com.application.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.application.model.User;
import com.application.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CurrentUserUtil {
    @Autowired
    private UserService userService;

    /**
     * Get the current authenticated user from the security context
     * @return User object of the currently logged-in user
     * @throws IllegalStateException if no user is authenticated
     */
    public User getCurrentUser() {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if authentication exists and is not anonymous
        if (authentication == null || !authentication.isAuthenticated() 
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("No user is currently authenticated");
        }

        // Extract username (email) from the authentication object
        String email = authentication.getName();

        // Fetch and return the user
        User user = userService.fetchUserByEmail(email);
        
        if (user == null) {
            throw new IllegalStateException("Authenticated user not found in database");
        }

        return user;
    }

    /**
     * Get the ID of the current authenticated user
     * @return Long user ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Get the role of the current authenticated user
     * @return String user role
     */
    public String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    /**
     * Check if the current user has a specific role
     * @param role Role to check
     * @return boolean indicating if the user has the specified role
     */
    public boolean hasRole(String role) {
        return role.equals(getCurrentUserRole());
    }

    /**
     * Check if the current user is an admin
     * @return boolean indicating if the user is an admin
     */
    public boolean isAdmin() {
        return hasRole("admin");
    }

    public boolean isDoctor() {
        return hasRole("doctor");
    }
}