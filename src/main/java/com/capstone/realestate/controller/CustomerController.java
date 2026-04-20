package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("All customers", customerService.listAllCustomers()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Customer>> myProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        int custId = getCustId(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Your profile", customerService.viewCustomer(custId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> viewCustomer(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Customer found", customerService.viewCustomer(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> editCustomer(
            @PathVariable int id, @RequestBody Customer customer) {
        customer.setCustId(id);
        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerService.editCustomer(customer)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> removeCustomer(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Customer removed", customerService.removeCustomer(id)));
    }

    private int getCustId(UserDetails userDetails) {
        var user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
        return customerRepository.findByUser_UserId(user.getUserId()).orElseThrow().getCustId();
    }
}
