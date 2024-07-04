package com.briteboxbackend.briterbox.service;


import com.briteboxbackend.briterbox.dto.ChangePasswordRequest;
import com.briteboxbackend.briterbox.dto.QRCodeGenerator;
import com.briteboxbackend.briterbox.dto.ReqRes;
import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.entities.Role;
import com.briteboxbackend.briterbox.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUpAsUser(ReqRes registrationRequest) {
        return signUpWithRole(registrationRequest, Role.ROLE_USER);
    }

    public ReqRes signUpAsAdmin(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            // Check if an admin already exists
            boolean adminExists = ourUserRepo.existsByRole(Role.ROLE_ADMIN);
            if (adminExists) {
                resp = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Admin already exists. Cannot create another admin.");
                return resp;
            }
            // Proceed with admin creation
            return signUpWithRole(registrationRequest, Role.ROLE_ADMIN);
        } catch (Exception e) {
            resp = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return resp;
        }
    }

    public ReqRes signUpAsEmployee(ReqRes registrationRequest) {
        return signUpWithRole(registrationRequest, Role.ROLE_EMPLOYEE);
    }

    private ReqRes signUpWithRole(ReqRes registrationRequest, Role role) {
        ReqRes resp = new ReqRes();
        try {
            // Your sign-up logic here
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setName(registrationRequest.getName());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setPhoneNumber(registrationRequest.getPhoneNumber());

            ourUsers.setDate(LocalDate.now());
            ourUsers.setRole(role);
            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                resp.setOurUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(HttpStatus.OK.value());
            }
        } catch (Exception e) {
            resp = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return resp;
    }

    public ReqRes signIn(ReqRes signinRequest){
        ReqRes response = new ReqRes();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getPhoneNumber(),signinRequest.getPassword()));
            var user = ourUserRepo.findByPhoneNumber(signinRequest.getPhoneNumber()).orElseThrow();
            System.out.println("USER IS: "+ user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(HttpStatus.OK.value());
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        }catch (Exception e){
            response = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByPhoneNumber(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(HttpStatus.OK.value());
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        } else {
            response = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token validation failed");
        }
        return response;
    }

    public ReqRes logout(String token) {
        ReqRes response = new ReqRes();
        try {
            tokenBlacklistService.blacklistToken(token);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Successfully logged out");
        } catch (Exception e) {
            response = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error during logout: " + e.getMessage());
        }
        return response;
    }

    public ReqRes changePassword(String phoneNumber, ChangePasswordRequest changePasswordRequest) {
        Optional<OurUsers> userOptional = ourUserRepo.findByPhoneNumber(phoneNumber);
        if (!userOptional.isPresent()) {
            return new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        OurUsers user = userOptional.get();

        // Validate old password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, changePasswordRequest.getOldPassword()));

        if (authentication.isAuthenticated()) {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            ourUserRepo.save(user);
            return new ReqRes(HttpStatus.OK.value(), "Password changed successfully");
        } else {
            return new ReqRes(HttpStatus.UNAUTHORIZED.value(), "Old password is incorrect");
        }
    }



}
