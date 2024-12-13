package com.application.dto;

import com.application.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private String username;
    private String mobile;
    private String bloodgroup;
    private String gender;
    private int age;
    private String role;

    public UserProfileDTO(User currentUser) {
        this.id = currentUser.getId();
        this.email = currentUser.getEmail();
        this.username = currentUser.getUsername();
        this.mobile = currentUser.getMobile();
        this.bloodgroup = currentUser.getBloodgroup();
        this.gender = currentUser.getGender();
        this.age = currentUser.getAge();
        this.role = currentUser.getRole();
    }
}
