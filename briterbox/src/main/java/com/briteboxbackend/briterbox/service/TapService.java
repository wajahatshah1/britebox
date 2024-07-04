package com.briteboxbackend.briterbox.service;

import com.briteboxbackend.briterbox.dto.BillRequest;
import com.briteboxbackend.briterbox.entities.Bill;
import com.briteboxbackend.briterbox.repository.BillRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class TapService {

    private static final Logger logger = LoggerFactory.getLogger(TapService.class);

    @Value("${tap.api.secret_key}")
    private String tapSecretKey;

    private static final String TAP_API_URL = "https://api.tap.company/v2/charges/";

    private final BillRepository billRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TapService(RestTemplate restTemplate, BillRepository billRepository) {
        this.restTemplate = restTemplate;
        this.billRepository = billRepository;
    }

    public String triggerTapPayment(long id, String firstName, String phoneNumber, double amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tapSecretKey);

        // Build the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("amount", amount);
            requestBody.put("currency", "BHD");
            requestBody.put("customer_initiated", true);
            requestBody.put("threeDSecure", true);
            requestBody.put("save_card", false);
            requestBody.put("description", "Test Description");
            requestBody.put("metadata", new JSONObject().put("orderid", id).put("udf1", "Metadata 1"));
            requestBody.put("receipt", new JSONObject().put("email", false).put("sms", true));
            JSONObject customer = new JSONObject()
                    .put("first_name", firstName)
                    .put("phone", new JSONObject().put("country_code", 973).put("number", phoneNumber));
            requestBody.put("customer", customer);
            requestBody.put("merchant", new JSONObject().put("id", "1234"));
            requestBody.put("source", new JSONObject().put("id", "src_all"));
            requestBody.put("post", new JSONObject().put("url", "https://8058-39-52-194-194.ngrok-free.app/payment/notify"));
            requestBody.put("redirect", new JSONObject().put("url", "http://192.168.10.201/"));
        } catch (JSONException e) {
            logger.error("Error creating JSON request body: {}", e.getMessage());
            return null;
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        System.out.println("Sending request to Tap API: " + requestBody);

        try {
            ResponseEntity<String> response = restTemplate.exchange(TAP_API_URL, HttpMethod.POST, entity, String.class);
            logger.info("Tap API Response: {}", response.getBody());
            System.out.println("Tap API Response: " + response.getBody());
            // Assuming the response contains a unique transaction ID
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String transactionId = jsonResponse.getString("id");
            System.out.println("Transaction ID: " + transactionId);

            return transactionId;
        } catch (Exception e) {
            logger.error("Error calling Tap API: {}", e.getMessage());
            System.out.println("Error calling Tap API: " + e.getMessage());
            return null;
        }
    }

    public String getTransactionUrl(String transactionId) {
        try {
            String url = TAP_API_URL + transactionId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tapSecretKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            logger.info("Tap API Transaction Details Response: {}", response.getBody());
            System.out.println("Tap API Transaction Details Response: " + response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getJSONObject("transaction").getString("url");
        } catch (Exception e) {
            logger.error("Error retrieving transaction URL: {}", e.getMessage());
            System.out.println("Error retrieving transaction URL: " + e.getMessage());
            return null;
        }
    }


}

