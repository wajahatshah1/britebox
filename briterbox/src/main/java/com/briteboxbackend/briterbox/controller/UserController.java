package com.briteboxbackend.briterbox.controller;


import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.repository.OurUserRepo;
import com.briteboxbackend.briterbox.service.AuthService;
import com.briteboxbackend.briterbox.service.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTUtils jwtUtils;



    @GetMapping("/profile")
    public ResponseEntity<?> getData(@RequestHeader("Authorization") String token) {
        // Extract username from token
        String username = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " prefix


        // Query data by username
        Optional<OurUsers> user = ourUserRepo.findByPhoneNumber(username);

        if (user.isPresent()) {
            // Print user details for debugging


            // Create a simplified response object with id, name, and phoneNumber
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", user.get().getId());
            responseData.put("name", user.get().getName());
            responseData.put("phoneNumber", user.get().getPhoneNumber());
            responseData.put("role", user.get().getRole());


            return ResponseEntity.ok(responseData);
        } else {
            System.out.println("User profile not found for username: " + username); // Debug print

            return ResponseEntity.notFound().build();
        }
    }




    @GetMapping("/admuserin/alone")
    public ResponseEntity<Object> userAlone(){
        return ResponseEntity.ok("Users alone can access this API only");
    }

    @GetMapping("/user/details")
    public String getCurrentUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); //get all details(name,email,password,roles e.t.c) of the user
        System.out.println(authentication.getDetails()); // get remote ip
        System.out.println(authentication.getName()); //returns the email because the email is the unique identifier
        return authentication.getName(); // returns the email
    }
}
