package com.capstone.realestate.controller;

import com.capstone.realestate.dto.AuthDTO;
import com.capstone.realestate.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerAdditionalTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnSuccessResponse() {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        req.setEmail("a@example.com");
        req.setPassword("Strong@123");
        req.setRole("CUSTOMER");

        AuthDTO.AuthResponse auth = new AuthDTO.AuthResponse();
        auth.setToken("tok");
        auth.setRole("CUSTOMER");
        when(userService.register(req)).thenReturn(auth);

        var response = authController.register(req);

        assertTrue(response.getBody().isSuccess());
        assertEquals("Registration successful", response.getBody().getMessage());
    }

    @Test
    void register_ShouldContainReturnedPayload() {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        AuthDTO.AuthResponse auth = new AuthDTO.AuthResponse();
        auth.setEmail("x@example.com");
        when(userService.register(req)).thenReturn(auth);

        var response = authController.register(req);

        assertNotNull(response.getBody().getData());
        assertEquals("x@example.com", response.getBody().getData().getEmail());
    }

    @Test
    void login_ShouldReturnSuccessMessage() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("u@example.com");
        req.setPassword("Strong@123");
        when(userService.login(req)).thenReturn(new AuthDTO.AuthResponse());

        var response = authController.login(req);

        assertEquals("Login successful", response.getBody().getMessage());
    }

    @Test
    void login_ShouldDelegateExactlyOnce() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        when(userService.login(req)).thenReturn(new AuthDTO.AuthResponse());

        authController.login(req);

        verify(userService, times(1)).login(req);
    }

    @Test
    void forgotPassword_ShouldReturnGenericMessage() {
        AuthDTO.ForgotPasswordRequest req = new AuthDTO.ForgotPasswordRequest();
        req.setEmail("u@example.com");

        var response = authController.forgotPassword(req);

        assertTrue(response.getBody().isSuccess());
        assertEquals("If the email exists, a reset link has been sent.", response.getBody().getMessage());
    }

    @Test
    void forgotPassword_ShouldDelegateToService() {
        AuthDTO.ForgotPasswordRequest req = new AuthDTO.ForgotPasswordRequest();

        authController.forgotPassword(req);

        verify(userService, times(1)).requestPasswordReset(req);
    }

    @Test
    void resetPassword_ShouldReturnSuccessMessage() {
        AuthDTO.ResetPasswordRequest req = new AuthDTO.ResetPasswordRequest();
        req.setToken("t");
        req.setPassword("Strong@123");
        req.setConfirmPassword("Strong@123");

        var response = authController.resetPassword(req);

        assertEquals("Password reset successful. Please login.", response.getBody().getMessage());
    }

    @Test
    void resetPassword_ShouldDelegateToService() {
        AuthDTO.ResetPasswordRequest req = new AuthDTO.ResetPasswordRequest();

        authController.resetPassword(req);

        verify(userService, times(1)).resetPassword(req);
    }

    @Test
    void login_WhenServiceThrows_ShouldPropagate() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        when(userService.login(req)).thenThrow(new IllegalArgumentException("Invalid credentials"));

        assertThrows(IllegalArgumentException.class, () -> authController.login(req));
    }

    @Test
    void register_WhenServiceThrows_ShouldPropagate() {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        when(userService.register(req)).thenThrow(new IllegalArgumentException("bad"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(req));
    }
}
