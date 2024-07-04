package com.briteboxbackend.briterbox.controller;


import com.briteboxbackend.briterbox.dto.SMSPayload;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class SmsController {

    @PostMapping("/send-sms")
    public ResponseEntity<String> sendSMS(@RequestBody SMSPayload payload) {
        String apiUrl = "https://messaging.stc.com.bh/bms/Soap/Messenger.asmx/HTTP_SendSms";

        // Encode message text to ensure special characters are properly handled
        String smsText = URLEncoder.encode(payload.getMessage(), StandardCharsets.UTF_8);

        // Construct the API URL with query parameters
        String apiRequestUrl = apiUrl +
                "?customerID=1469" +
                "&userName=BriteBox" +
                "&userPassword=Brite123" +
                "&originator=BriteBox" +
                "&smsText=" + smsText +
                "&recipientPhone=" + payload.getPhoneNumber() +
                "&messageType=Latin" +
                "&defDate=" +
                "&blink=false" +
                "&flash=false" +
                "&Private=false";

        // Print API request URL for debugging
        System.out.println("API Request URL: " + apiRequestUrl);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                apiRequestUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Print response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("SMS sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send SMS. Response: " + responseEntity.getBody());
        }
    }
}
