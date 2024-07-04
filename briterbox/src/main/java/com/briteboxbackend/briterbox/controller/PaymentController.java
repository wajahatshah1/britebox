package com.briteboxbackend.briterbox.controller;

import com.briteboxbackend.briterbox.dto.BillRequest;
import com.briteboxbackend.briterbox.dto.PaymentRequest;
 // Assuming your entity is named Bill
import com.briteboxbackend.briterbox.entities.Bill;
import com.briteboxbackend.briterbox.repository.BillRepository;
import com.briteboxbackend.briterbox.service.BillService;
import com.briteboxbackend.briterbox.service.TapService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private TapService tapService;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillService billService;


    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/charge")
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        String transactionId = tapService.triggerTapPayment(
                paymentRequest.getId(),
                paymentRequest.getPhoneNumber(),
                paymentRequest.getFirstName(),
                paymentRequest.getAmount()
        );
        if (transactionId != null) {
            // Save the transaction ID in the Bill database
            billService.updateBillWithTransactionId(paymentRequest.getId(), transactionId);
            return ResponseEntity.ok(transactionId);
        } else {
            return ResponseEntity.status(500).body("Payment initiation failed");
        }
    }

    @GetMapping("/transaction-url/{transactionId}")
    public ResponseEntity<String> getTransactionUrl(@PathVariable String transactionId) {
        String url = tapService.getTransactionUrl(transactionId);
        if (url != null) {
            return ResponseEntity.ok(url);
        } else {
            return ResponseEntity.status(500).body("Failed to retrieve transaction URL");
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handlePaymentNotification(@RequestBody String payload) {
        logger.info("Received payment notification: {}", payload);

        try {
            JSONObject jsonPayload = new JSONObject(payload);
            String transactionId = jsonPayload.getString("id");
            String status = jsonPayload.getString("status");
            long orderId = jsonPayload.getJSONObject("metadata").getLong("orderid");

            // Find the bill associated with the order ID
            Bill bill = billRepository.findById(orderId).orElse(null);
            if (bill == null) {
                logger.error("Bill not found for order ID: {}", orderId);
                return ResponseEntity.badRequest().body("Bill not found");
            }

            // Update the bill status and transaction ID
            if ("CAPTURED".equalsIgnoreCase(status)) {
                bill.setStatus("paid");
            } else {
                logger.info("Ignoring notification with status: {}", status);
                return ResponseEntity.ok("Notification received but ignored due to status: " + status);
            }
            billRepository.save(bill);

            logger.info("Updated bill for order ID: {} with status: {} and transaction ID: {}", orderId, status, transactionId);

            // Respond with a success message
            return ResponseEntity.ok("Notification received and processed successfully");
        } catch (Exception e) {
            logger.error("Error processing payment notification: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error processing notification");
        }
    }


}
