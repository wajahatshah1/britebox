package com.briteboxbackend.briterbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String firstName;
    private String phoneNumber;
    private double amount;
    private long id;


}
