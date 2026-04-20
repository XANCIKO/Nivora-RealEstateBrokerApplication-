package com.capstone.realestate.service;

import com.capstone.realestate.entity.Customer;

import java.util.List;

public interface ICustomerService {
    Customer addCustomer(Customer customer);
    Customer editCustomer(Customer customer);
    Customer removeCustomer(int custId);
    Customer viewCustomer(int custId);
    List<Customer> listAllCustomers();
}
