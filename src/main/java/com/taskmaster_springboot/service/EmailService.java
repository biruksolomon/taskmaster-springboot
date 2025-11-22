package com.taskmaster_springboot.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}
