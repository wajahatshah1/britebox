package com.briteboxbackend.briterbox.controller;


;
import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.entities.Role;
import com.briteboxbackend.briterbox.entities.Tax;
import com.briteboxbackend.briterbox.repository.OurUserRepo;
import com.briteboxbackend.briterbox.repository.TaxRepository;
import com.briteboxbackend.briterbox.service.AuthService;
import com.briteboxbackend.briterbox.service.JWTUtils;
import com.briteboxbackend.briterbox.service.TaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TaxService service;

    @GetMapping("/checkadmin")
    public ResponseEntity<Object> AdminAlone() {
        return ResponseEntity.ok("Users alone can access this API only");
    }

    @PutMapping("/updateRate")
    public Tax updateTaxRate(@RequestParam String value) {
        // Assuming there's only one tax entity in the system, and its ID is 1
        Long taxId = 1L;
        Tax tax = taxRepository.findById(taxId).orElseThrow(() -> new RuntimeException("Tax not found"));
        tax.setValue(value);
        return taxRepository.save(tax);
    }

    @GetMapping("/current")
    public ResponseEntity<String> getCurrentTax() {
        Tax tax = taxRepository.findById(1L).orElse(null);
        if (tax != null) {
            return ResponseEntity.ok(tax.getValue());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getAllData(@RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        List<OurUsers> users = ourUserRepo.findByRole(Role.ROLE_EMPLOYEE);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
