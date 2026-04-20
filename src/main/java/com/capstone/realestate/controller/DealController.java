package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.entity.Deal;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.IDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final IDealService dealService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    // Customer - buy or rent a property
    @PostMapping("/property/{propId}")
    public ResponseEntity<ApiResponse<Deal>> createDeal(
            @PathVariable int propId,
            @AuthenticationPrincipal UserDetails userDetails) {
        int custId = getCustId(userDetails);
        Deal deal = dealService.addDeal(propId, custId);
        return ResponseEntity.ok(ApiResponse.success("Deal created successfully!", deal));
    }

    // Customer - view my deals (owned/rented properties)
    @GetMapping("/my-deals")
    public ResponseEntity<ApiResponse<List<Deal>>> myDeals(
            @AuthenticationPrincipal UserDetails userDetails) {
        int custId = getCustId(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Your deals", dealService.listDealsByCustomer(custId)));
    }

    // All deals (admin-like view)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Deal>>> allDeals() {
        return ResponseEntity.ok(ApiResponse.success("All deals", dealService.listAllDeals()));
    }

    private int getCustId(UserDetails userDetails) {
        var user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
        return customerRepository.findByUser_UserId(user.getUserId()).orElseThrow().getCustId();
    }
}
