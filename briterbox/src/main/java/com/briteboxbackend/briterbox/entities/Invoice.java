package com.briteboxbackend.briterbox.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity class representing the invoice table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Lob
    @Column(name = "pdf_data", columnDefinition = "BLOB")
    private byte[] pdfData;



    // getters and setters
}

// Repository interface for invoice entity

// Controller class to handle HTTP requests
