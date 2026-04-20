package com.capstone.realestate.dto;

import lombok.*;
import java.time.LocalDate;

public class DealDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DealResponse {
        private int dealId;
        private LocalDate dealDate;
        private double dealCost;
        private String address;
        private String street;
        private String city;
        private int areaSqft;
        private String offerType;
    }
}
