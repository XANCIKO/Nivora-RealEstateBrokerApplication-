package com.capstone.realestate.service.impl;

import com.capstone.realestate.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromAddress;

    @Override
    public void sendPasswordResetOtp(String recipientEmail, String otp) {
        if (mailHost == null || mailHost.isBlank()) {
            throw new IllegalStateException("Email sending is not configured. Set spring.mail.* properties first.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(recipientEmail);
        message.setSubject("Your Nivora password reset OTP");
        message.setText("We received a request to reset your Nivora account password.\n\n"
            + "Your OTP is: " + otp + "\n\n"
            + "This OTP expires in 10 minutes. If you did not request this, you can ignore this email.");
        mailSender.send(message);
    }
}