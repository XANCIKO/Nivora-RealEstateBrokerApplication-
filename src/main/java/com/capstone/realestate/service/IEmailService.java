package com.capstone.realestate.service;

public interface IEmailService {
    void sendPasswordResetOtp(String recipientEmail, String otp);
}