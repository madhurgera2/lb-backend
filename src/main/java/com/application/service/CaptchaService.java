package com.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {
	private static final String SECRET_KEY = "YOUR_RECAPTCHA_SECRET_KEY";
	private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	public boolean verifyCaptcha(String captchaToken) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("secret", SECRET_KEY);
		requestBody.put("response", captchaToken);

		Map response = restTemplate.postForObject(VERIFY_URL, requestBody, Map.class);
		return response != null && Boolean.TRUE.equals(response.get("success"));
	}
}
