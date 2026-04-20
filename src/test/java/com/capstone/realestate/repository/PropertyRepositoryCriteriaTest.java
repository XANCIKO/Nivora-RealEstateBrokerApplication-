package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PropertyRepositoryCriteriaTest {

    @Autowired private PropertyRepository propertyRepository;
    @Autowired private BrokerRepository brokerRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void findByCriteria_ShouldFilterByOfferAndCityPrefix() {
        User u = new User();
        u.setEmail("criteria1@example.com");
        u.setPassword("Secret@123");
        u.setRole("BROKER");
        u = userRepository.save(u);

        Broker b = new Broker();
        b.setBroName("B");
        b.setUser(u);
        b = brokerRepository.save(b);

        Property saleInChennai = new Property();
        saleInChennai.setConfiguration("2BHK");
        saleInChennai.setOfferType("SALE");
        saleInChennai.setOfferCost(1200);
        saleInChennai.setAreaSqft(800);
        saleInChennai.setAddress("A1");
        saleInChennai.setStreet("S1");
        saleInChennai.setCity("Chennai");
        saleInChennai.setStatus(true);
        saleInChennai.setBroker(b);
        propertyRepository.save(saleInChennai);

        Property rentInDelhi = new Property();
        rentInDelhi.setConfiguration("2BHK");
        rentInDelhi.setOfferType("RENT");
        rentInDelhi.setOfferCost(1400);
        rentInDelhi.setAreaSqft(900);
        rentInDelhi.setAddress("A2");
        rentInDelhi.setStreet("S2");
        rentInDelhi.setCity("Delhi");
        rentInDelhi.setStatus(true);
        rentInDelhi.setBroker(b);
        propertyRepository.save(rentInDelhi);

        List<Property> out = propertyRepository.findByCriteria("2BHK", "SALE", "Che", 0, 0);

        assertEquals(1, out.size());
        assertEquals("Chennai", out.get(0).getCity());
    }

    @Test
    void findByCriteria_ShouldApplyCostBounds() {
        User u = new User();
        u.setEmail("criteria2@example.com");
        u.setPassword("Secret@123");
        u.setRole("BROKER");
        u = userRepository.save(u);

        Broker b = new Broker();
        b.setBroName("B2");
        b.setUser(u);
        b = brokerRepository.save(b);

        Property p = new Property();
        p.setConfiguration("3BHK");
        p.setOfferType("SALE");
        p.setOfferCost(5000);
        p.setAreaSqft(1000);
        p.setAddress("A3");
        p.setStreet("S3");
        p.setCity("Coimbatore");
        p.setStatus(true);
        p.setBroker(b);
        propertyRepository.save(p);

        List<Property> inRange = propertyRepository.findByCriteria("3BHK", "SALE", "Coi", 4500, 5500);
        List<Property> outRange = propertyRepository.findByCriteria("3BHK", "SALE", "Coi", 6000, 9000);

        assertTrue(inRange.size() >= 1);
        assertEquals(0, outRange.size());
    }
}
