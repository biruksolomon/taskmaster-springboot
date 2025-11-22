package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.application.name}")
    private String platformname;

    @Value("${spring.mail.host}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toemail, String verificationCode) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toemail);
            helper.setSubject(platformname + " - Email Verification Required ✨");
            helper.setText(verificationCode, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send verification email to: " + toemail + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
