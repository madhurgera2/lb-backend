package com.application.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

@Service
public class SendGridEmailService {

	private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

	@Autowired
	private SendGrid sendGrid;

	@Value("${sendgrid.from-email}")
	private String fromEmail;

	@Value("${sendgrid.from-name}")
	private String fromName;

	// Send simple email
	public String sendEmail(String toEmail, String subject, String body) {
		if (!isValidEmail(toEmail)) {
			logger.error("Invalid email address: {}", toEmail);
			return "Invalid email address.";
		}

		Email from = new Email(fromEmail, fromName);
		Email to = new Email(toEmail);
		Content content = new Content("text/plain", body);
		Mail mail = new Mail(from, subject, to, content);

		return sendMail(mail);
	}

	// Send email with attachment
	public String sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentName,
			byte[] attachmentData) {
		if (!isValidEmail(toEmail)) {
			logger.error("Invalid email address: {}", toEmail);
			return "Invalid email address.";
		}

		Email from = new Email(fromEmail, fromName);
		Email to = new Email(toEmail);
		Content content = new Content("text/plain", body);
		Mail mail = new Mail(from, subject, to, content);

		// Add attachment
		String mimeType = getMimeType(attachmentName);
		Attachments attachment = new Attachments();
		attachment.setFilename(attachmentName);
		attachment.setType(mimeType);
		attachment.setDisposition("attachment");
		attachment.setContent(Base64.getEncoder().encodeToString(attachmentData));
		mail.addAttachments(attachment);

		return sendMail(mail);
	}

	// Send email with dynamic template
	public String sendDynamicTemplateEmail(String toEmail, String templateId, Map<String, String> dynamicData) {
		if (!isValidEmail(toEmail)) {
			logger.error("Invalid email address: {}", toEmail);
			return "Invalid email address.";
		}

		Email from = new Email(fromEmail, fromName);
		Email to = new Email(toEmail);
		Mail mail = new Mail();
		mail.setFrom(from);
		mail.setTemplateId(templateId);

		Personalization personalization = new Personalization();
		personalization.addTo(to);

		// Add dynamic data (e.g., {{name}}, {{link}})
		for (Map.Entry<String, String> entry : dynamicData.entrySet()) {
			personalization.addDynamicTemplateData(entry.getKey(), entry.getValue());
		}

		mail.addPersonalization(personalization);
		return sendMail(mail);
	}

	// Helper method to send email via SendGrid API
	private String sendMail(Mail mail) {
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sendGrid.api(request);

			// Log the response code using format specifiers
			if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
				logger.info("Email sent successfully. Response code: {}", response.getStatusCode());
			} else {
				logger.warn("Email failed to send. Response code: {}, Body: {}", response.getStatusCode(),
						response.getBody());
			}

			return "Email sent. Status code: " + response.getStatusCode();
		} catch (IOException ex) {
			// Log the error using format specifiers
			logger.error("Error occurred while sending email: {}", ex.getMessage(), ex);
			return "Error occurred while sending email: " + ex.getMessage();
		}
	}

	// Helper method to get MIME type of a file (for attachments)
	private String getMimeType(String filePath) {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		return mimeTypesMap.getContentType(filePath);
	}

	// Helper method to validate email address
	private boolean isValidEmail(String email) {
		if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			return false;
		}
		return true;
	}
}
