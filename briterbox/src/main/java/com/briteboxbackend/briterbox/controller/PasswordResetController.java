package com.briteboxbackend.briterbox.controller;

import com.briteboxbackend.briterbox.dto.VerifyResetCodeRequest;
import com.briteboxbackend.briterbox.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String phoneNumber) {
        try {
            passwordResetService.initiatePasswordReset(phoneNumber);
            return ResponseEntity.ok("Password reset initiated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<String> verifyResetCode(@RequestBody VerifyResetCodeRequest request) {
        boolean isValid = passwordResetService.verifyResetCode(request.getPhoneNumber(), request.getResetCode());
        if (isValid) {
            // Process password reset (e.g., redirect to reset password form)
            return ResponseEntity.ok("Reset code verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid reset code");
        }
    }
}