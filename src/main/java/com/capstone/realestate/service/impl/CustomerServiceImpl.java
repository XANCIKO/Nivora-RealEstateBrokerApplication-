package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer editCustomer(Customer customer) {
        customerRepository.findById(customer.getCustId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customer.getCustId()));
        return customerRepository.save(customer);
    }

    @Override
    public Customer removeCustomer(int custId) {
        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + custId));
        customerRepository.delete(customer);
        return customer;
    }

    @Override
    public Customer viewCustomer(int custId) {
        return customerRepository.findById(custId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + custId));
    }

    @Override
    public List<Customer> listAllCustomers() {
        return customerRepository.findAll();
    }
}
