package com.capstone.realestate.repository;

import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryAdditionalTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByPasswordResetToken_ShouldReturnMatchingUser() {
        User u = new User();
        u.setEmail("token1@example.com");
        u.setPassword("Strong@123");
        u.setRole("CUSTOMER");
        u.setPasswordResetToken("tok-1");
        u.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(u);

        Optional<User> out = userRepository.findByPasswordResetToken("tok-1");

        assertTrue(out.isPresent());
    }

    @Test
    void findByPasswordResetToken_WhenMissing_ShouldReturnEmpty() {
        Optional<User> out = userRepository.findByPasswordResetToken("no-token");

        assertTrue(out.isEmpty());
    }

    @Test
    void findByEmailIgnoreCase_ShouldWorkWithMixedCase() {
        User u = new User();
        u.setEmail("MixCase@example.com");
        u.setPassword("Strong@123");
        u.setRole("CUSTOMER");
        userRepository.save(u);

        Optional<User> out = userRepository.findByEmailIgnoreCase("mixcase@EXAMPLE.com");

        assertTrue(out.isPresent());
    }

    @Test
    void existsByEmailIgnoreCase_WhenAbsent_ShouldReturnFalse() {
        assertFalse(userRepository.existsByEmailIgnoreCase("absent@example.com"));
    }

    @Test
    void save_ShouldPersistCityAndMobile() {
        User u = new User();
        u.setEmail("meta@example.com");
        u.setPassword("Strong@123");
        u.setRole("CUSTOMER");
        u.setCity("Chennai");
        u.setMobile("9876543210");

        User out = userRepository.save(u);

        assertEquals("Chennai", out.getCity());
        assertEquals("9876543210", out.getMobile());
    }

    @Test
    void save_ShouldAssignPrimaryKey() {
        User u = new User();
        u.setEmail("pk@example.com");
        u.setPassword("Strong@123");
        u.setRole("CUSTOMER");

        User out = userRepository.save(u);

        assertTrue(out.getUserId() > 0);
    }
}
