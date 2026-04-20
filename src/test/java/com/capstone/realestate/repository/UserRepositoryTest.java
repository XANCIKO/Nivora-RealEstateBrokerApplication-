package com.capstone.realestate.repository;

import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailIgnoreCase_ShouldFindUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("Secret@123");
        user.setRole("CUSTOMER");
        userRepository.save(user);

        Optional<User> out = userRepository.findByEmailIgnoreCase("USER@example.com");

        assertTrue(out.isPresent());
    }

    @Test
    void existsByEmailIgnoreCase_ShouldReturnTrue() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPassword("Secret@123");
        user.setRole("CUSTOMER");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmailIgnoreCase("EXISTS@example.com");

        assertTrue(exists);
    }
}
