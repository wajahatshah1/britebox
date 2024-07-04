package com.briteboxbackend.briterbox.controller;


import com.briteboxbackend.briterbox.entities.Invoice;
import com.briteboxbackend.briterbox.repository.invoiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("admin/invoices")
public class InvoiceController {


    private invoiceRepository invoiceRepository0;


    @PostMapping("/save")
    public ResponseEntity<String> saveInvoice(@RequestParam String customerName,
                                              @RequestParam MultipartFile pdfData) {
        try {
            byte[] pdfBytes = pdfData.getBytes();
            Invoice invoice = new Invoice();
            invoice.setCustomerName(customerName);
            invoice.setPdfData(pdfBytes);
            invoiceRepository0.save(invoice);
            return ResponseEntity.ok("Invoice saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving invoice.");
        }
    }
}