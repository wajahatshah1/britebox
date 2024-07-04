package com.briteboxbackend.briterbox.service;

import com.briteboxbackend.briterbox.entities.Invoice;
import com.briteboxbackend.briterbox.repository.invoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class InvoiceService {

    @Autowired
    private invoiceRepository pdfRepository;

    public Invoice getInvoiceByName(String customerName) throws FileNotFoundException {
        Invoice invoice = (Invoice) pdfRepository.findByCustomerName(customerName);
        // You might need to handle potential exceptions during BLOB retrieval
        return invoice;
    }
}

