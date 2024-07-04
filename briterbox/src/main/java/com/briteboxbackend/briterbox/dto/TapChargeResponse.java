package com.briteboxbackend.briterbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TapChargeResponse {
    private String status;
    private String message;
    private String charge_id;
    // Add other fields as per your JSON response
    // Add getters and setters
}
