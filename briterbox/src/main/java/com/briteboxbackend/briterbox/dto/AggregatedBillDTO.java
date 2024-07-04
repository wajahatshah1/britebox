package com.briteboxbackend.briterbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AggregatedBillDTO {
    private LocalDate date;
    private String totalAmountWithTax;

}

