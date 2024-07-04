package com.briteboxbackend.briterbox.service;//package com.britebox.springbootBackend.service;
//
//
//
//import com.britebox.springbootBackend.repository.DoorService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@Service
//public class DoorServiceImpl implements DoorService {
//
//    private static final Logger logger = LoggerFactory.getLogger(DoorServiceImpl.class);
//
//    private final RestTemplate restTemplate;
//    private final String apiKey;
//    private final String sn;
//    private final String apiUrl;
//
//    public DoorServiceImpl(RestTemplate restTemplate,
//                           @Value("${api.key}") String apiKey,
//                           @Value("${api.sn}") String sn,
//                           @Value("${api.url}") String apiUrl) {
//        this.restTemplate = restTemplate;
//        this.apiKey = apiKey;
//        this.sn = sn;
//        this.apiUrl = apiUrl;
//    }
//
//    @Override
//    public void openDoor(int doorNumber) {
//        try {
//            logger.info("Opening door {}...", doorNumber);
//
//            // Prepare the API request URL
//            String requestUrl = UriComponentsBuilder.fromUriString(apiUrl)
//                    .queryParam("APIKey", apiKey)
//                    .queryParam("Action", "SetDoorOpen")
//                    .queryParam("Data.SN", sn)
//                    .queryParam("Data.DoorNo", doorNumber)
//                    .build().toUriString();
//
//            // Make the HTTP request to the external API using RestTemplate
//            restTemplate.getForEntity(requestUrl, null, String.class);
//
//            logger.info("Door {} opened successfully.", doorNumber);
//        } catch (HttpServerErrorException e) {
//            logger.error("Failed to open door {}. Server returned error: {}", doorNumber, e.getRawStatusCode());
//            // Handle the error as needed
//        } catch (Exception e) {
//            logger.error("An unexpected error occurred while opening door {}: {}", doorNumber, e.getMessage());
//            // Handle the error as needed
//        }
//    }
//}
