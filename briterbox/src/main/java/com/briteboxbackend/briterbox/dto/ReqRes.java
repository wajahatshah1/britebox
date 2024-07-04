package com.briteboxbackend.briterbox.dto;



import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {
    private long id;
    private int statusCode;
    private String notes;
    private LocalDate date;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private String password;
    private OurUsers ourUsers;
    private String region;
    private String customerId;

    public ReqRes(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }


}
