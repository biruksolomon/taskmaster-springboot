package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.application.name}")
    private String platformname;

    @Value("${spring.mail.from:noreply@taskmaster.com}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String toemail, String verificationCode) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toemail);
            helper.setSubject(platformname + " - Email Verification Required ✨");

            String emailBody = buildVerificationEmailBody(verificationCode);
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toemail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {} - {}", toemail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(platformname + " - Password Reset Request");

            String emailBody = buildPasswordResetEmailBody(token);
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {} - {}", email, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildVerificationEmailBody(String verificationCode) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2>Email Verification Required</h2>" +
                "<p>Thank you for registering with " + platformname + "!</p>" +
                "<p>Please use the following code to verify your email:</p>" +
                "<h3 style='background-color: #f0f0f0; padding: 10px; text-align: center;'>" + verificationCode + "</h3>" +
                "<p style='color: #666;'>This code will expire in 20 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best regards,<br>" + platformname + " Team</p>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetEmailBody(String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        return "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2>Password Reset Request</h2>" +
                "<p>We received a request to reset your " + platformname + " password.</p>" +
                "<p>Click the button below to reset your password:</p>" +
                "<p><a href='" + resetLink + "' style='background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a></p>" +
                "<p style='color: #666;'>This link will expire in 1 hour.</p>" +
                "<p>If you did not request this, please ignore this email or contact support.</p>" +
                "<p>Best regards,<br>" + platformname + " Team</p>" +
                "</body>" +
                "</html>";
    }
}
