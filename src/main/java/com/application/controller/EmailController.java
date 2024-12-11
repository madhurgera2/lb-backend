package com.application.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.service.SendGridEmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private SendGridEmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String toEmail, 
                            @RequestParam String subject, 
                            @RequestParam String body) {
        return emailService.sendEmail(toEmail, subject, body);
    }
    
    @PostMapping("/send-with-attachment")
    public String sendEmailWithAttachment(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam String attachmentPath
    ) throws IOException {
        byte[] attachmentData = Files.readAllBytes(Paths.get(attachmentPath));
        return emailService.sendEmailWithAttachment(toEmail, subject, body, attachmentPath, attachmentData);
    }

    @PostMapping("/send-dynamic-template")
    public String sendDynamicTemplateEmail(
            @RequestParam String toEmail,
            @RequestParam String templateId,
            @RequestBody Map<String, String> dynamicData
    ) {
        return emailService.sendDynamicTemplateEmail(toEmail, templateId, dynamicData);
    }
}
