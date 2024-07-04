package com.briteboxbackend.briterbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillRequest {

    private String pdf;
    private String customerId;
    private String name;
    private String phoneNumber;
    private String status;
    private LocalDate date;
    private String time;
    private String  totalAmountWithTax;
    private boolean detail;
    private boolean cleaning;
    private boolean ready;
    private boolean pickup;
    private boolean picked;
    private String notes;
    private long id;
    private String transactionId;
    // Getters and setters
}

