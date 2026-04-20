package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.Deal;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.IDealService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private IDealService dealService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private DealController dealController;

    @Test
    void createDeal_ShouldResolveCustomerAndDelegate() {
        User u = new User();
        u.setUserId(14);

        Customer c = new Customer();
        c.setCustId(44);

        Deal d = new Deal();

        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(14)).thenReturn(Optional.of(c));
        when(dealService.addDeal(9, 44)).thenReturn(d);

        var response = dealController.createDeal(9, userDetails);

        assertNotNull(response);
        verify(dealService, times(1)).addDeal(9, 44);
    }

    @Test
    void myDeals_ShouldResolveCustomerAndDelegate() {
        User u = new User();
        u.setUserId(3);
        Customer c = new Customer();
        c.setCustId(7);

        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(3)).thenReturn(Optional.of(c));
        when(dealService.listDealsByCustomer(7)).thenReturn(List.of(new Deal()));

        var response = dealController.myDeals(userDetails);

        assertNotNull(response);
        verify(dealService, times(1)).listDealsByCustomer(7);
    }

    @Test
    void allDeals_ShouldDelegateToService() {
        when(dealService.listAllDeals()).thenReturn(List.of(new Deal()));

        var response = dealController.allDeals();

        assertNotNull(response);
        verify(dealService, times(1)).listAllDeals();
    }
}
