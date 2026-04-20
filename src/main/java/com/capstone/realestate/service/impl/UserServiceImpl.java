package com.capstone.realestate.service.impl;

import com.capstone.realestate.dto.AuthDTO;
import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.security.JwtUtil;
import com.capstone.realestate.service.IEmailService;
import com.capstone.realestate.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BrokerRepository brokerRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
        private final IEmailService emailService;

        @Value("${app.frontend-url:http://localhost:5173}")
        private String frontendUrl;

    @Override
    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
                String normalizedEmail = normalizeEmail(request.getEmail());

                if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                        throw new IllegalArgumentException("Email already registered: " + normalizedEmail);
        }

        validatePassword(request.getPassword());
                validateMobile(request.getMobile());

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .mobile(request.getMobile())
                .city(request.getCity())
                .build();

        userRepository.save(user);

        // Create role-specific profile
        if ("BROKER".equalsIgnoreCase(request.getRole())) {
            Broker broker = Broker.builder()
                    .broName(request.getName())
                    .user(user)
                    .build();
            brokerRepository.save(broker);
        } else if ("CUSTOMER".equalsIgnoreCase(request.getRole())) {
            Customer customer = Customer.builder()
                    .custName(request.getName())
                    .user(user)
                    .build();
            customerRepository.save(customer);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .email(user.getEmail())
                .name(request.getName())
                .build();
    }

    @Override
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        String name = "";
        if ("BROKER".equals(user.getRole())) {
            name = brokerRepository.findByUser_UserId(user.getUserId())
                    .map(Broker::getBroName).orElse("");
        } else {
            name = customerRepository.findByUser_UserId(user.getUserId())
                    .map(Customer::getCustName).orElse("");
        }

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .email(user.getEmail())
                .name(name)
                .build();
	}

        @Override
        @Transactional
        public void requestPasswordReset(AuthDTO.ForgotPasswordRequest request) {
                String normalizedEmail = normalizeEmail(request.getEmail());

                User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Email is not registered."));

                String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
                user.setPasswordResetToken(otp);
                user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
                userRepository.save(user);

                emailService.sendPasswordResetOtp(user.getEmail(), otp);
        }

        @Override
        @Transactional(readOnly = true)
        public void verifyPasswordResetOtp(AuthDTO.VerifyResetOtpRequest request) {
                resolveAndValidateOtpUser(request.getEmail(), request.getOtp());
        }

        @Override
        @Transactional
        public void resetPassword(AuthDTO.ResetPasswordRequest request) {
                String password = request.getPassword() == null ? "" : request.getPassword();
                String confirmPassword = request.getConfirmPassword() == null ? "" : request.getConfirmPassword();

                if (!password.equals(confirmPassword)) {
                        throw new IllegalArgumentException("Passwords do not match.");
                }

                validatePassword(password);

                String token = request.getToken() == null ? "" : request.getToken().trim();

                User user;
                if (!normalizeEmail(request.getEmail()).isBlank() && request.getOtp() != null && !request.getOtp().trim().isBlank()) {
                        user = resolveAndValidateOtpUser(request.getEmail(), request.getOtp());
                } else {
                        if (token.isBlank()) {
                                throw new IllegalArgumentException("OTP is required.");
                        }

                        user = userRepository.findByPasswordResetToken(token)
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token."));
                }

                if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
                        user.setPasswordResetToken(null);
                        user.setPasswordResetTokenExpiry(null);
                        userRepository.save(user);
                        throw new IllegalArgumentException("OTP has expired. Please request a new one.");
                }

                user.setPassword(passwordEncoder.encode(password));
                user.setPasswordResetToken(null);
                user.setPasswordResetTokenExpiry(null);
                userRepository.save(user);
        }

        private User resolveAndValidateOtpUser(String email, String otp) {
                String normalizedEmail = normalizeEmail(email);
                String normalizedOtp = otp == null ? "" : otp.trim();

                if (normalizedEmail.isBlank() || normalizedOtp.isBlank()) {
                        throw new IllegalArgumentException("Email and OTP are required.");
                }

                User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP or email."));

                if (user.getPasswordResetToken() == null || !normalizedOtp.equals(user.getPasswordResetToken())) {
                        throw new IllegalArgumentException("Invalid OTP or email.");
                }

                if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
                        user.setPasswordResetToken(null);
                        user.setPasswordResetTokenExpiry(null);
                        userRepository.save(user);
                        throw new IllegalArgumentException("OTP has expired. Please request a new one.");
                }

                return user;
        }

        private String normalizeEmail(String email) {
                return email == null ? "" : email.trim().toLowerCase();
        }

        private void validatePassword(String password) {
                StringBuilder errors = new StringBuilder();
                String value = password == null ? "" : password;

                boolean hasUppercase = false;
                boolean hasLowercase = false;
                boolean hasDigit = false;
                boolean hasSpecial = false;
                String specialChars = "!@#$%^&*()_+-=[]{};':\"\\|,.<>/?";

                for (char ch : value.toCharArray()) {
                        if (Character.isUpperCase(ch)) {
                                hasUppercase = true;
                        } else if (Character.isLowerCase(ch)) {
                                hasLowercase = true;
                        } else if (Character.isDigit(ch)) {
                                hasDigit = true;
                        }

                        if (specialChars.indexOf(ch) >= 0) {
                                hasSpecial = true;
                        }
                }

                if (value.length() < 8) {
                        errors.append("Password must be at least 8 characters. ");
                }
                if (!hasUppercase) {
                        errors.append("Password must contain an uppercase letter. ");
                }
                if (!hasLowercase) {
                        errors.append("Password must contain a lowercase letter. ");
                }
                if (!hasDigit) {
                        errors.append("Password must contain a number. ");
                }
                if (!hasSpecial) {
                        errors.append("Password must contain a special character (!@#$%^&*). ");
                }

                if (errors.length() > 0) {
                        throw new IllegalArgumentException(errors.toString().trim());
                }

        }

        private void validateMobile(String mobile) {
                if (mobile == null || !mobile.matches("\\d{10}")) {
                        throw new IllegalArgumentException("Mobile number must be exactly 10 digits.");
        }
        }
}
