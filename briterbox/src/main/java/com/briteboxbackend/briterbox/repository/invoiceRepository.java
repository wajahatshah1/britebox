package com.briteboxbackend.briterbox.repository;



;

import com.briteboxbackend.briterbox.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface invoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerName(String customerName);
}


