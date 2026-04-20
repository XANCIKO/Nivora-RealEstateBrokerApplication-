package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplAdditionalTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void addCustomer_ShouldReturnSavedEntity() {
        Customer c = new Customer();
        c.setCustName("C");
        when(customerRepository.save(c)).thenReturn(c);

        Customer out = customerService.addCustomer(c);

        assertEquals("C", out.getCustName());
    }

    @Test
    void addCustomer_ShouldCallSaveOnce() {
        Customer c = new Customer();
        when(customerRepository.save(c)).thenReturn(c);

        customerService.addCustomer(c);

        verify(customerRepository, times(1)).save(c);
    }

    @Test
    void editCustomer_ShouldThrowWhenMissing() {
        Customer c = new Customer();
        c.setCustId(5);
        when(customerRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.editCustomer(c));
    }

    @Test
    void editCustomer_ShouldSaveWhenFound() {
        Customer c = new Customer();
        c.setCustId(5);
        when(customerRepository.findById(5)).thenReturn(Optional.of(c));
        when(customerRepository.save(c)).thenReturn(c);

        Customer out = customerService.editCustomer(c);

        assertEquals(5, out.getCustId());
    }

    @Test
    void removeCustomer_ShouldThrowWhenMissing() {
        when(customerRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.removeCustomer(10));
    }

    @Test
    void removeCustomer_ShouldDeleteWhenFound() {
        Customer c = new Customer();
        c.setCustId(7);
        when(customerRepository.findById(7)).thenReturn(Optional.of(c));

        customerService.removeCustomer(7);

        verify(customerRepository).delete(c);
    }

    @Test
    void viewCustomer_ShouldReturnFoundEntity() {
        Customer c = new Customer();
        c.setCustId(11);
        when(customerRepository.findById(11)).thenReturn(Optional.of(c));

        Customer out = customerService.viewCustomer(11);

        assertEquals(11, out.getCustId());
    }

    @Test
    void listAllCustomers_ShouldReturnCollection() {
        when(customerRepository.findAll()).thenReturn(java.util.List.of(new Customer(), new Customer()));

        var out = customerService.listAllCustomers();

        assertEquals(2, out.size());
    }
}
