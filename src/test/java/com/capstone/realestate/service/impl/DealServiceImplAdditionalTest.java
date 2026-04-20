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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplAdditionalTest {

    @Mock private DealRepository dealRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    void addDeal_WhenPropertyMissing_ShouldThrowNotFound() {
        when(propertyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.addDeal(1, 2));
    }

    @Test
    void addDeal_WhenCustomerMissing_ShouldThrowNotFound() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(1000);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));
        when(customerRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.addDeal(1, 2));
    }

    @Test
    void addDeal_WhenUnavailable_ShouldThrow() {
        Property p = new Property();
        p.setStatus(false);
        when(propertyRepository.findById(2)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> dealService.addDeal(2, 3));
    }

    @Test
    void addDeal_WhenRent_ShouldUseFullPayment() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(2500);
        Customer c = new Customer();

        when(propertyRepository.findById(3)).thenReturn(Optional.of(p));
        when(customerRepository.findById(4)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal out = dealService.addDeal(3, 4);

        assertEquals(2500.0, out.getDealCost());
        assertEquals(0.0, out.getRemainingAmount());
    }

    @Test
    void addDeal_WhenSale_ShouldUseEightPercentAdvance() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(10000);
        Customer c = new Customer();

        when(propertyRepository.findById(5)).thenReturn(Optional.of(p));
        when(customerRepository.findById(6)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal out = dealService.addDeal(5, 6);

        assertEquals(800.0, out.getDealCost());
        assertEquals(9200.0, out.getRemainingAmount());
        assertEquals(8.0, out.getAdvancePercent());
    }

    @Test
    void addDeal_ShouldPersistPropertyStatusChange() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(1000);
        Customer c = new Customer();

        when(propertyRepository.findById(7)).thenReturn(Optional.of(p));
        when(customerRepository.findById(8)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        dealService.addDeal(7, 8);

        verify(propertyRepository).save(p);
        assertFalse(p.isStatus());
    }

    @Test
    void addDeal_ShouldSetDealDate() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(1000);
        Customer c = new Customer();

        when(propertyRepository.findById(9)).thenReturn(Optional.of(p));
        when(customerRepository.findById(10)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal out = dealService.addDeal(9, 10);

        assertNotNull(out.getDealDate());
    }

    @Test
    void addDeal_ShouldCaptureSavedDealWithExpectedTotals() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(50000);
        Customer c = new Customer();

        when(propertyRepository.findById(11)).thenReturn(Optional.of(p));
        when(customerRepository.findById(12)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        dealService.addDeal(11, 12);

        ArgumentCaptor<Deal> cap = ArgumentCaptor.forClass(Deal.class);
        verify(dealRepository).save(cap.capture());
        assertEquals(50000.0, cap.getValue().getTotalAmount());
    }

    @Test
    void listAllDeals_ShouldDelegate() {
        when(dealRepository.findAll()).thenReturn(java.util.List.of(new Deal()));

        var out = dealService.listAllDeals();

        assertEquals(1, out.size());
    }

    @Test
    void listDealsByCustomer_ShouldDelegate() {
        when(dealRepository.findByCustomer_CustId(13)).thenReturn(java.util.List.of(new Deal(), new Deal()));

        var out = dealService.listDealsByCustomer(13);

        assertEquals(2, out.size());
    }
}
