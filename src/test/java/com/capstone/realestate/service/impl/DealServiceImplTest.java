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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    void addDeal_WhenPropertyMissing_ShouldThrow() {
        when(propertyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.addDeal(1, 2));
    }

    @Test
    void addDeal_WhenPropertyUnavailable_ShouldThrow() {
        Property p = new Property();
        p.setStatus(false);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> dealService.addDeal(1, 2));
    }

    @Test
    void addDeal_WhenValidSale_ShouldCreateDealAndMarkPropertyUnavailable() {
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(1000);

        Customer c = new Customer();

        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));
        when(customerRepository.findById(2)).thenReturn(Optional.of(c));
        when(dealRepository.save(any(Deal.class))).thenAnswer(inv -> inv.getArgument(0));

        Deal deal = dealService.addDeal(1, 2);

        assertEquals(80.0, deal.getDealCost());
        assertEquals(920.0, deal.getRemainingAmount());
        verify(propertyRepository, times(1)).save(p);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }

    @Test
    void listAllDeals_ShouldDelegate() {
        when(dealRepository.findAll()).thenReturn(List.of(new Deal(), new Deal()));

        List<Deal> out = dealService.listAllDeals();

        assertEquals(2, out.size());
    }

    @Test
    void listDealsByCustomer_ShouldDelegate() {
        when(dealRepository.findByCustomer_CustId(8)).thenReturn(List.of(new Deal()));

        List<Deal> out = dealService.listDealsByCustomer(8);

        assertEquals(1, out.size());
    }
}
