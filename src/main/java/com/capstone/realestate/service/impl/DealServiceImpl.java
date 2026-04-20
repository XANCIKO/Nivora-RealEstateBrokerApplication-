package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.Deal;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.DealRepository;
import com.capstone.realestate.repository.PropertyRepository;
import com.capstone.realestate.service.IDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements IDealService {

    private static final double SALE_ADVANCE_PERCENT = 8.0;

    private final DealRepository dealRepository;
    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Deal addDeal(int propId, int custId) {
        Property property = propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));

        if (!property.isStatus()) {
            throw new IllegalArgumentException("Property is no longer available for purchase/rent.");
        }

        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + custId));

        // Mark property as unavailable
        property.setStatus(false);
        propertyRepository.save(property);

        boolean saleOffer = "SALE".equalsIgnoreCase(property.getOfferType());
        double totalAmount = property.getOfferCost();
        double paidAmount = saleOffer
            ? (totalAmount * SALE_ADVANCE_PERCENT / 100.0)
            : totalAmount;
        double remainingAmount = Math.max(0.0, totalAmount - paidAmount);

        Deal deal = Deal.builder()
                .dealDate(LocalDate.now())
            .dealCost(paidAmount)
            .totalAmount(totalAmount)
            .remainingAmount(remainingAmount)
            .advancePercent(saleOffer ? SALE_ADVANCE_PERCENT : 100.0)
                .customer(customer)
                .property(property)
                .build();

        return dealRepository.save(deal);
    }

    @Override
    public List<Deal> listAllDeals() {
        return dealRepository.findAll();
    }

    @Override
    public List<Deal> listDealsByCustomer(int custId) {
        return dealRepository.findByCustomer_CustId(custId);
    }
}
