package com.briteboxbackend.briterbox.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String notes;
    private String customerId;
    private String name;
    private String phoneNumber;
    private String status;
    private LocalDate date;
    private String totalAmountWithTax;
    private boolean detail;
    private boolean cleaning;
    private boolean ready;
    private boolean pickup;
    private boolean picked;
    private String transactionId;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] pdf;

    // Getters and setters
}
