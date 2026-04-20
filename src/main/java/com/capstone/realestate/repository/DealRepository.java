package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Integer> {
    List<Deal> findByCustomer_CustId(int custId);
}
