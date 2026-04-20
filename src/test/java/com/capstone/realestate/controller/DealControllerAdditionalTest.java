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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerAdditionalTest {

    @Mock private IDealService dealService;
    @Mock private UserRepository userRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private DealController dealController;

    @Test
    void allDeals_ShouldReturnSuccessMessage() {
        when(dealService.listAllDeals()).thenReturn(List.of());

        var response = dealController.allDeals();

        assertEquals("All deals", response.getBody().getMessage());
    }

    @Test
    void createDeal_ShouldUseResolvedCustomerId() {
        User u = new User(); u.setUserId(1);
        Customer c = new Customer(); c.setCustId(77);
        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(1)).thenReturn(Optional.of(c));
        when(dealService.addDeal(10, 77)).thenReturn(new Deal());

        dealController.createDeal(10, userDetails);

        verify(dealService).addDeal(10, 77);
    }

    @Test
    void createDeal_ShouldReturnCreatedMessage() {
        User u = new User(); u.setUserId(1);
        Customer c = new Customer(); c.setCustId(2);
        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(1)).thenReturn(Optional.of(c));
        when(dealService.addDeal(5, 2)).thenReturn(new Deal());

        var response = dealController.createDeal(5, userDetails);

        assertEquals("Deal created successfully!", response.getBody().getMessage());
    }

    @Test
    void myDeals_ShouldReturnYourDealsMessage() {
        User u = new User(); u.setUserId(1);
        Customer c = new Customer(); c.setCustId(2);
        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(1)).thenReturn(Optional.of(c));
        when(dealService.listDealsByCustomer(2)).thenReturn(List.of(new Deal()));

        var response = dealController.myDeals(userDetails);

        assertEquals("Your deals", response.getBody().getMessage());
    }

    @Test
    void myDeals_ShouldDelegateOnce() {
        User u = new User(); u.setUserId(4);
        Customer c = new Customer(); c.setCustId(9);
        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(4)).thenReturn(Optional.of(c));
        when(dealService.listDealsByCustomer(9)).thenReturn(List.of());

        dealController.myDeals(userDetails);

        verify(dealService, times(1)).listDealsByCustomer(9);
    }

    @Test
    void createDeal_WhenUserMissing_ShouldThrow() {
        when(userDetails.getUsername()).thenReturn("missing@example.com");
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> dealController.createDeal(1, userDetails));
    }

    @Test
    void myDeals_WhenCustomerMissing_ShouldThrow() {
        User u = new User(); u.setUserId(4);
        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(4)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> dealController.myDeals(userDetails));
    }

    @Test
    void allDeals_ShouldReturnDataList() {
        when(dealService.listAllDeals()).thenReturn(List.of(new Deal(), new Deal()));

        var response = dealController.allDeals();

        assertEquals(2, response.getBody().getData().size());
    }
}
