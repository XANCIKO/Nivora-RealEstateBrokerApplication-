package com.capstone.realestate.dto;

import lombok.*;

public class AuthDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        private String email;
        private String password;
        private String role; // BROKER or CUSTOMER
        private String name;
        private String mobile;
        private String city;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ForgotPasswordRequest {
        private String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifyResetOtpRequest {
        private String email;
        private String otp;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ResetPasswordRequest {
        private String email;
        private String otp;
        private String token;
        private String password;
        private String confirmPassword;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private String role;
        private String email;
        private String name;
    }
}
