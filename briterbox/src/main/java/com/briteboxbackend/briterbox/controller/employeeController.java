package com.briteboxbackend.briterbox.controller;



import com.briteboxbackend.briterbox.dto.AccountsDTO;
import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.entities.Role;
import com.briteboxbackend.briterbox.repository.OurUserRepo;
import com.briteboxbackend.briterbox.service.AuthService;
import com.briteboxbackend.briterbox.service.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee")
public class employeeController {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthService authService;


    @GetMapping("/profile")
    public ResponseEntity<?> getData(@RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        Optional<OurUsers> user = ourUserRepo.findByPhoneNumber(username);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getAllData(@RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        List<OurUsers> users = ourUserRepo.findByRole(Role.ROLE_USER);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{phoneNumber}")
    public ResponseEntity<?> getCustomerByPhoneNumberCustomer(@PathVariable String phoneNumber, @RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        Optional<OurUsers> user = ourUserRepo.findByPhoneNumberAndRole(phoneNumber, Role.ROLE_USER);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/alone")
    public ResponseEntity<Object> userAlone() {
        return ResponseEntity.ok("Users alone can access this API only");
    }

    @GetMapping("/records")
    public List<AccountsDTO> getRecords() {
        System.out.println("Fetching users with role USER");
        List<OurUsers> users = ourUserRepo.findByRole(Role.ROLE_USER);
        System.out.println("Fetched " + users.size() + " users with role USER");

        users = users.stream()
                .filter(user -> {
                    boolean valid = user.getDate() != null && user.getPhoneNumber() != null;
                    if (!valid) {
                        System.out.println("Filtered out user with ID: " + user.getId() + " due to null date or phone number");
                    }
                    return valid;
                })
                .collect(Collectors.toList());
        System.out.println("Filtered users list size: " + users.size());

        Map<LocalDate, Map<String, Long>> accountsData = users.stream()
                .collect(Collectors.groupingBy(OurUsers::getDate,
                        Collectors.groupingBy(OurUsers::getPhoneNumber, Collectors.counting())));
        System.out.println("Aggregated data by date and phone number");

        List<AccountsDTO> result = new ArrayList<>();
        accountsData.forEach((date, phoneNumbers) -> {
            long totalPhoneNumbers = phoneNumbers.values().stream().mapToLong(Long::longValue).sum();
            result.add(new AccountsDTO(date, totalPhoneNumbers));
            System.out.println("Added AccountsDTO for date " + date + ": total phone numbers = " + totalPhoneNumbers);
        });

        System.out.println("Returning result with size: " + result.size());
        return result;
    }

    @GetMapping("/details")
    public String getCurrentUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); //get all details(name,email,password,roles e.t.c) of the user
        System.out.println(authentication.getDetails()); // get remote ip
        System.out.println(authentication.getName()); //returns the email because the email is the unique identifier
        return authentication.getName(); // returns the email
    }
}
