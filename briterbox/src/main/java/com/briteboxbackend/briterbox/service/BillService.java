package com.briteboxbackend.briterbox.service;



import com.briteboxbackend.briterbox.entities.Bill;
import com.briteboxbackend.briterbox.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    @Autowired
    BillRepository billRepository;

    @Autowired
    TapService tapService;

    public List<Bill> getAllbills() {
        try {
            return billRepository.findAll();
        } catch (Exception e) {
            // Handle any exceptions that might occur during the saving process
            e.printStackTrace();
            return null;
        }
    }


    public void updateBillWithTransactionId(long id, String transactionId) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setTransactionId(transactionId);
            billRepository.save(bill);
        } else {
            throw new IllegalArgumentException("Bill with ID " + id + " not found");
        }
    }

    public void updateBillStatusToPaid(long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setStatus("paid");
            billRepository.save(bill);
        } else {
            throw new IllegalArgumentException("Bill with ID " + id + " not found");
        }
    }


}
