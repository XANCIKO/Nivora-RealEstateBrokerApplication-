package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.ICustomerService;
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
class CustomerControllerAdditionalTest {

    @Mock private ICustomerService customerService;
    @Mock private UserRepository userRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void listAll_ShouldReturnAllCustomersMessage() {
        when(customerService.listAllCustomers()).thenReturn(List.of());

        var response = customerController.listAll();

        assertEquals("All customers", response.getBody().getMessage());
    }

    @Test
    void viewCustomer_ShouldDelegateToService() {
        when(customerService.viewCustomer(8)).thenReturn(new Customer());

        customerController.viewCustomer(8);

        verify(customerService).viewCustomer(8);
    }

    @Test
    void viewCustomer_ShouldReturnSuccess() {
        when(customerService.viewCustomer(2)).thenReturn(new Customer());

        var response = customerController.viewCustomer(2);

        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void editCustomer_ShouldOverridePathId() {
        Customer c = new Customer();
        c.setCustId(55);
        when(customerService.editCustomer(any(Customer.class))).thenReturn(c);

        customerController.editCustomer(4, c);

        assertEquals(4, c.getCustId());
    }

    @Test
    void removeCustomer_ShouldDelegate() {
        when(customerService.removeCustomer(1)).thenReturn(new Customer());

        customerController.removeCustomer(1);

        verify(customerService).removeCustomer(1);
    }

    @Test
    void myProfile_ShouldResolveCustomerFromUser() {
        User u = new User();
        u.setUserId(10);
        Customer c = new Customer();
        c.setCustId(20);
        when(userDetails.getUsername()).thenReturn("u@example.com");
        when(userRepository.findByEmailIgnoreCase("u@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(10)).thenReturn(Optional.of(c));
        when(customerService.viewCustomer(20)).thenReturn(c);

        var response = customerController.myProfile(userDetails);

        assertEquals(20, response.getBody().getData().getCustId());
    }

    @Test
    void myProfile_WhenUserNotFound_ShouldThrow() {
        when(userDetails.getUsername()).thenReturn("missing@example.com");
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> customerController.myProfile(userDetails));
    }

    @Test
    void myProfile_WhenCustomerNotFound_ShouldThrow() {
        User u = new User();
        u.setUserId(1);
        when(userDetails.getUsername()).thenReturn("u@example.com");
        when(userRepository.findByEmailIgnoreCase("u@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(1)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> customerController.myProfile(userDetails));
    }
}
