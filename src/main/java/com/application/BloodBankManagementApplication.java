package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class BloodBankManagementApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(BloodBankManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BloodBankManagementApplication.class, args);
		logger.info("BloodBankManagementApplication has started successfully!");
	}

}
