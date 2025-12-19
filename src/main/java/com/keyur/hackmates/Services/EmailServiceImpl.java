package com.keyur.hackmates.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    //fields
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {

        String subject = "Your Hackmates OTP Code";
        String body = "Hello,\n\n" +
                "Your OTP for email verification is: " + otp + "\n" +
                "This OTP will expire in 15 minutes.\n\n" +
                "If you did not register, please ignore this email.\n\n" +
                "Best regards,\nHackmates Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("no-reply@hackmates.com"); // optional

        mailSender.send(message);
    }
}

