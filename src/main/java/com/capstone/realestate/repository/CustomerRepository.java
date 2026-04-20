package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUser_UserId(int userId);
}
