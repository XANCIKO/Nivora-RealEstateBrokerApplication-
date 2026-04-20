package com.capstone.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrokerContactDto {
    private Integer brokerId;
    private String name;
    private String email;
    private String mobile;
}
