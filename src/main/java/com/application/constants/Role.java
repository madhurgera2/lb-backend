package com.application.constants;

public enum Role {
    USER("user"),
    VOLUNTEER("volunteer"),
    ADMIN("admin"),
    SUPER_ADMIN("superAdmin");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
    
    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.role.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}
