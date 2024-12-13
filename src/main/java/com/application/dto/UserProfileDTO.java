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

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.mobile = user.getMobile();
        this.bloodgroup = user.getBloodgroup();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.role = user.getRole();
    }
}
