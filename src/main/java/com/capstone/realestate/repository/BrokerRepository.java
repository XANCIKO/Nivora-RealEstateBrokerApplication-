package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Integer> {
    Optional<Broker> findByUser_UserId(int userId);
}
