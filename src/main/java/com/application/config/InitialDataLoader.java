package com.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.application.constants.Role;
import com.application.model.User;
import com.application.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean alreadySetup = false;

    @Value("${app.initial-data.enabled:true}")
    private boolean initialDataEnabled;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialDataEnabled ||alreadySetup) {
            return;
        }

        // Create initial users if they don't exist
        createUserIfNotExists("admin@example.com", "Admin User", Role.ADMIN.getRole());
        createUserIfNotExists("dean@example.com", "Dean User", Role.DEAN.getRole());
        createUserIfNotExists("user@example.com", "Regular User", Role.USER.getRole());

        alreadySetup = true;
    }

    @Transactional
    private void createUserIfNotExists(String email, String name, String role) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            User user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setBloodgroup("A+");
            user.setMobile("1234567890");
            user.setGender("Male");
            user.setAge(25);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode("password123")); // Default secure password
            user.setRole(role);
            
            userRepository.save(user);
            logger.info("Created {} user: {}", role, email);
        }
    }
}