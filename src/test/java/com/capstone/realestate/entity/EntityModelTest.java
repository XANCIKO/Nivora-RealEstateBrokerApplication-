package com.capstone.realestate.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityModelTest {

    @Test
    void user_ShouldStoreEmail() {
        User u = new User();
        u.setEmail("u@example.com");
        assertEquals("u@example.com", u.getEmail());
    }

    @Test
    void user_ShouldStoreResetTokenExpiry() {
        User u = new User();
        LocalDateTime now = LocalDateTime.now();
        u.setPasswordResetTokenExpiry(now);
        assertEquals(now, u.getPasswordResetTokenExpiry());
    }

    @Test
    void broker_ShouldStoreName() {
        Broker b = new Broker();
        b.setBroName("B");
        assertEquals("B", b.getBroName());
    }

    @Test
    void customer_ShouldStoreName() {
        Customer c = new Customer();
        c.setCustName("C");
        assertEquals("C", c.getCustName());
    }

    @Test
    void property_ShouldStoreOfferType() {
        Property p = new Property();
        p.setOfferType("SALE");
        assertEquals("SALE", p.getOfferType());
    }

    @Test
    void property_ShouldStoreOfferCost() {
        Property p = new Property();
        p.setOfferCost(12345.0);
        assertEquals(12345.0, p.getOfferCost());
    }

    @Test
    void property_ShouldStoreArea() {
        Property p = new Property();
        p.setAreaSqft(900.5);
        assertEquals(900.5, p.getAreaSqft());
    }

    @Test
    void property_ShouldStoreStatus() {
        Property p = new Property();
        p.setStatus(false);
        assertFalse(p.isStatus());
    }

    @Test
    void deal_ShouldStoreDate() {
        Deal d = new Deal();
        LocalDate date = LocalDate.now();
        d.setDealDate(date);
        assertEquals(date, d.getDealDate());
    }

    @Test
    void deal_ShouldStoreTotals() {
        Deal d = new Deal();
        d.setTotalAmount(5000.0);
        assertEquals(5000.0, d.getTotalAmount());
    }

    @Test
    void deal_ShouldStoreAdvancePercent() {
        Deal d = new Deal();
        d.setAdvancePercent(8.0);
        assertEquals(8.0, d.getAdvancePercent());
    }

    @Test
    void deal_ShouldStoreRemainingAmount() {
        Deal d = new Deal();
        d.setRemainingAmount(1200.0);
        assertEquals(1200.0, d.getRemainingAmount());
    }
}
