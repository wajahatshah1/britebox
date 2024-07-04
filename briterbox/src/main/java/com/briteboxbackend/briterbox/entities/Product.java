package com.briteboxbackend.briterbox.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String section;
    private boolean active;
    private boolean online;
    private boolean taxExempt;
    private double price;
    private double expressPrice;
    private String image;


}
