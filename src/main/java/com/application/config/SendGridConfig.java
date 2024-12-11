package com.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;

@Configuration
public class SendGridConfig {

	@Value("${sendgrid.api-key}")
	private String sendGridApiKey;

	private static final Logger logger = LoggerFactory.getLogger(SendGridConfig.class);

	@Bean
	public SendGrid sendGrid() {
		if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
			logger.error("SendGrid API key is not set in application.properties!");
			throw new IllegalArgumentException("SendGrid API key is missing.");
		}

		logger.info("SendGrid API key successfully loaded.");

		return new SendGrid(sendGridApiKey);
	}
}
