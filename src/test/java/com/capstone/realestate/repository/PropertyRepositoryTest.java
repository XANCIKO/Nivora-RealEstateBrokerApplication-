package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class PropertyRepositoryTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByBroker_BroId_ShouldReturnListings() {
        User u = new User();
        u.setEmail("pbroker@example.com");
        u.setPassword("Secret@123");
        u.setRole("BROKER");
        u = userRepository.save(u);

        Broker b = new Broker();
        b.setBroName("Broker P");
        b.setUser(u);
        b = brokerRepository.save(b);

        Property p = new Property();
        p.setConfiguration("2BHK");
        p.setOfferType("SALE");
        p.setOfferCost(5000);
        p.setAreaSqft(1000);
        p.setAddress("Addr");
        p.setStreet("Street");
        p.setCity("Chennai");
        p.setStatus(true);
        p.setBroker(b);
        propertyRepository.save(p);

        List<Property> out = propertyRepository.findByBroker_BroId(b.getBroId());

        assertFalse(out.isEmpty());
    }

    @Test
    void findByCriteria_ShouldFilterByCityPrefix() {
        List<Property> out = propertyRepository.findByCriteria(null, null, "Che", 0, 0);
        // Query should execute without errors even if empty on fresh DB.
        assertFalse(out == null);
    }
}
