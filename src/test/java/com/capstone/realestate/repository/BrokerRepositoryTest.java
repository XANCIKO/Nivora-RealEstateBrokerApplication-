package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BrokerRepositoryTest {

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUser_UserId_ShouldFindBroker() {
        User user = new User();
        user.setEmail("broker@example.com");
        user.setPassword("Secret@123");
        user.setRole("BROKER");
        User savedUser = userRepository.save(user);

        Broker broker = new Broker();
        broker.setBroName("B1");
        broker.setUser(savedUser);
        brokerRepository.save(broker);

        Optional<Broker> out = brokerRepository.findByUser_UserId(savedUser.getUserId());

        assertTrue(out.isPresent());
    }
}
