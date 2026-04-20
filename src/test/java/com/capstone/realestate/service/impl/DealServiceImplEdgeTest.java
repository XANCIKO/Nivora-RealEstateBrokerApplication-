package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.Deal;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.DealRepository;
import com.capstone.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceImplEdgeTest {

    @Mock private DealRepository dealRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    void addDeal_WhenCustomerMissing_ShouldThrow() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(1000);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));
        when(customerRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.addDeal(1, 2));
    }

    @Test
    void addDeal_WhenRentOffer_ShouldChargeFullAmount() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(5000);

        Customer c = new Customer();

        when(propertyRepository.findById(3)).thenReturn(Optional.of(p));
        when(customerRepository.findById(4)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal out = dealService.addDeal(3, 4);

        assertEquals(5000.0, out.getDealCost());
        assertEquals(0.0, out.getRemainingAmount());
        assertEquals(100.0, out.getAdvancePercent());
    }

    @Test
    void addDeal_WhenSaleOffer_ShouldChargeEightPercentAdvance() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(250000);

        Customer c = new Customer();

        when(propertyRepository.findById(7)).thenReturn(Optional.of(p));
        when(customerRepository.findById(8)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal out = dealService.addDeal(7, 8);

        assertEquals(20000.0, out.getDealCost());
        assertEquals(230000.0, out.getRemainingAmount());
        assertEquals(8.0, out.getAdvancePercent());
    }

    @Test
    void addDeal_ShouldMarkPropertyUnavailableBeforeSavingDeal() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(1000);

        Customer c = new Customer();

        when(propertyRepository.findById(5)).thenReturn(Optional.of(p));
        when(customerRepository.findById(6)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        dealService.addDeal(5, 6);

        assertEquals(false, p.isStatus());
    }
}
