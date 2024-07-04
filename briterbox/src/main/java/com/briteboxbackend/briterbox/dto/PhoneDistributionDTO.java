package com.briteboxbackend.briterbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhoneDistributionDTO {
    private String phoneNumber;
    private double percentage;
}

