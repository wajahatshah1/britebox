package com.briteboxbackend.briterbox.service;


import com.briteboxbackend.briterbox.entities.Tax;
import com.briteboxbackend.briterbox.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

;

@Service
public class TaxService {

    @Autowired
    private TaxRepository repository;

    public Tax updateTaxRate(Long id, String value) {
        Tax tax = repository.findById(id).orElse(null);
        if (tax != null) {
            tax.setValue(value);
            return repository.save(tax);
        }
        return null;
    }
}
