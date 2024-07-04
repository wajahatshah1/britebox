package com.briteboxbackend.briterbox.service;

import com.briteboxbackend.briterbox.entities.OurUsers;

import com.briteboxbackend.briterbox.repository.OurUserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private OurUserRepo userRepository;

    private Map<String, String> resetCodeMap = new HashMap<>(); // Store phone number and reset code

    // Method to initiate password reset process and send the 6-digit reset code
    public void initiatePasswordReset(String phoneNumber) {
        Optional<OurUsers> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            String resetCode = generateResetCode();
            resetCodeMap.put(phoneNumber, resetCode); // Store reset code for verification
            sendResetCode(phoneNumber, resetCode); // Simulate sending the reset code
        } else {
            throw new IllegalArgumentException("Phone number not registered");
        }
    }

    // Method to generate a random 6-digit reset code
    private String generateResetCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate a random number between 100000 and 999999
        return String.valueOf(code);
    }

    // Simulated method to send reset code (print to console for demonstration)
    private void sendResetCode(String phoneNumber, String resetCode) {
        // In a real application, this method should send the reset code via SMS, email, or any other notification method
        System.out.println("Sending reset code to " + phoneNumber + ": " + resetCode);
    }

    // Method to verify and process the reset code
    public boolean verifyResetCode(String phoneNumber, String resetCode) {
        String storedCode = resetCodeMap.get(phoneNumber);
        return storedCode != null && storedCode.equals(resetCode);
    }
}
