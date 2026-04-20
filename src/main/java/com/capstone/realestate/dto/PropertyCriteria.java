package com.capstone.realestate.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyCriteria {
    private String config;   // Flat/Shop/Plot
    private String offer;    // Sell/Rent
    private String city;
    private double minCost;
    private double maxCost;
}
