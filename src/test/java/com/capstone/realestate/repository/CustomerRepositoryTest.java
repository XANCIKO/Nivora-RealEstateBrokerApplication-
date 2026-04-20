package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUser_UserId_ShouldFindCustomer() {
        User user = new User();
        user.setEmail("customer@example.com");
        user.setPassword("Secret@123");
        user.setRole("CUSTOMER");
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setCustName("C1");
        customer.setUser(savedUser);
        customerRepository.save(customer);

        Optional<Customer> out = customerRepository.findByUser_UserId(savedUser.getUserId());

        assertTrue(out.isPresent());
    }
}
