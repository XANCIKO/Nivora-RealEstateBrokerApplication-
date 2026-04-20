package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void addCustomer_ShouldSave() {
        Customer c = new Customer();
        c.setCustName("C1");
        when(customerRepository.save(c)).thenReturn(c);

        Customer out = customerService.addCustomer(c);

        assertEquals("C1", out.getCustName());
        verify(customerRepository, times(1)).save(c);
    }

    @Test
    void editCustomer_WhenMissing_ShouldThrow() {
        Customer c = new Customer();
        c.setCustId(10);
        when(customerRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.editCustomer(c));
    }

    @Test
    void removeCustomer_WhenExists_ShouldDelete() {
        Customer c = new Customer();
        c.setCustId(7);
        when(customerRepository.findById(7)).thenReturn(Optional.of(c));

        Customer out = customerService.removeCustomer(7);

        assertEquals(7, out.getCustId());
        verify(customerRepository, times(1)).delete(c);
    }

    @Test
    void viewCustomer_WhenMissing_ShouldThrow() {
        when(customerRepository.findById(44)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.viewCustomer(44));
    }

    @Test
    void listAllCustomers_ShouldReturnAll() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer()));

        List<Customer> out = customerService.listAllCustomers();

        assertEquals(1, out.size());
    }
}
