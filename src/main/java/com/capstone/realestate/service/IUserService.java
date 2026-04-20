package com.capstone.realestate.service;

import com.capstone.realestate.dto.AuthDTO;

public interface IUserService {
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);
    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);
    void requestPasswordReset(AuthDTO.ForgotPasswordRequest request);
    void verifyPasswordResetOtp(AuthDTO.VerifyResetOtpRequest request);
    void resetPassword(AuthDTO.ResetPasswordRequest request);
}
