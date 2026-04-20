package com.capstone.realestate.controller;

import com.capstone.realestate.dto.AuthDTO;
import com.capstone.realestate.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldDelegateToUserService() {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        req.setEmail("user@example.com");
        req.setPassword("Pass@123");
        req.setRole("CUSTOMER");

        AuthDTO.AuthResponse res = new AuthDTO.AuthResponse();
        res.setToken("token123");
        when(userService.register(req)).thenReturn(res);

        var response = authController.register(req);

        assertNotNull(response);
        verify(userService, times(1)).register(req);
    }

    @Test
    void login_ShouldDelegateToUserService() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("Pass@123");

        AuthDTO.AuthResponse res = new AuthDTO.AuthResponse();
        res.setToken("token123");
        when(userService.login(req)).thenReturn(res);

        var response = authController.login(req);

        assertNotNull(response);
        verify(userService, times(1)).login(req);
    }

    @Test
    void forgotPassword_ShouldDelegateToUserService() {
        AuthDTO.ForgotPasswordRequest req = new AuthDTO.ForgotPasswordRequest();
        req.setEmail("user@example.com");

        var response = authController.forgotPassword(req);

        assertNotNull(response);
        verify(userService, times(1)).requestPasswordReset(req);
    }

    @Test
    void resetPassword_ShouldDelegateToUserService() {
        AuthDTO.ResetPasswordRequest req = new AuthDTO.ResetPasswordRequest();
        req.setToken("token");
        req.setPassword("NewPass@123");
        req.setConfirmPassword("NewPass@123");

        var response = authController.resetPassword(req);

        assertNotNull(response);
        verify(userService, times(1)).resetPassword(req);
    }
}
