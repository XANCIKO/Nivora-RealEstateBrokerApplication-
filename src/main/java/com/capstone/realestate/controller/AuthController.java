package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.dto.AuthDTO;
import com.capstone.realestate.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> register(
            @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.AuthResponse response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> login(
            @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.AuthResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody AuthDTO.ForgotPasswordRequest request) {
        userService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your registered email.", null));
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<Void>> verifyResetOtp(
            @RequestBody AuthDTO.VerifyResetOtpRequest request) {
        userService.verifyPasswordResetOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody AuthDTO.ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful. Please login.", null));
    }
}
