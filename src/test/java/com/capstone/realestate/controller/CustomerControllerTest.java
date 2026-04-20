package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.ICustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private ICustomerService customerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void listAll_ShouldReturnResponse() {
        when(customerService.listAllCustomers()).thenReturn(List.of(new Customer()));

        var response = customerController.listAll();

        assertNotNull(response);
        verify(customerService, times(1)).listAllCustomers();
    }

    @Test
    void myProfile_ShouldResolveCustomerIdAndDelegate() {
        User u = new User();
        u.setUserId(25);
        u.setEmail("cust@example.com");

        Customer c = new Customer();
        c.setCustId(41);

        when(userDetails.getUsername()).thenReturn("cust@example.com");
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));
        when(customerRepository.findByUser_UserId(25)).thenReturn(Optional.of(c));
        when(customerService.viewCustomer(41)).thenReturn(c);

        var response = customerController.myProfile(userDetails);

        assertNotNull(response);
        verify(customerService, times(1)).viewCustomer(41);
    }

    @Test
    void editCustomer_ShouldSetPathIdOnPayload() {
        Customer payload = new Customer();

        customerController.editCustomer(9, payload);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).editCustomer(captor.capture());
        assertEquals(9, captor.getValue().getCustId());
    }

    @Test
    void removeCustomer_ShouldDelegateToService() {
        when(customerService.removeCustomer(13)).thenReturn(new Customer());

        var response = customerController.removeCustomer(13);

        assertNotNull(response);
        verify(customerService, times(1)).removeCustomer(13);
    }
}
