package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.Deal;
import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class DealRepositoryTest {

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByCustomer_CustId_ShouldReturnDeals() {
        User u = new User();
        u.setEmail("dealcust@example.com");
        u.setPassword("Secret@123");
        u.setRole("CUSTOMER");
        u = userRepository.save(u);

        Customer c = new Customer();
        c.setCustName("Deal Cust");
        c.setUser(u);
        c = customerRepository.save(c);

        Deal d = new Deal();
        d.setCustomer(c);
        d.setDealCost(1000);
        d.setTotalAmount(1000);
        d.setRemainingAmount(0);
        d.setAdvancePercent(100);
        dealRepository.save(d);

        List<Deal> out = dealRepository.findByCustomer_CustId(c.getCustId());

        assertFalse(out.isEmpty());
    }
}
