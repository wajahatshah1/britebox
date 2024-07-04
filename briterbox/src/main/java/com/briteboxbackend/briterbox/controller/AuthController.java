package com.briteboxbackend.briterbox.controller;


import com.briteboxbackend.briterbox.dto.ChangePasswordRequest;
import com.briteboxbackend.briterbox.dto.QRCodeGenerator;
import com.briteboxbackend.briterbox.dto.ReqRes;
import com.briteboxbackend.briterbox.entities.OurUsers;
import com.briteboxbackend.briterbox.repository.OurUserRepo;
import com.briteboxbackend.briterbox.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private RestTemplate restTemplate;

     // Inject RestTemplate or use WebClient, HttpClient, etc.



        @PostMapping("/userSignin")
        public ResponseEntity<Map<String, Object>> signInAsUser(@RequestBody ReqRes signinRequest) {
            ReqRes response = authService.signIn(signinRequest);

            // Check if sign-in was successful (you can adjust this condition based on your response)
            if (response.getStatusCode() == 200) {
                // Call the /profile endpoint
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + response.getToken()); // Assuming token is returned in response

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<OurUsers> profileResponse = restTemplate.exchange(
                        "http://192.168.10.201:8080/user/profile", // Replace with your endpoint URL
                        HttpMethod.GET,
                        entity,
                        OurUsers.class
                );

                // Handle profileResponse according to your application's logic
                if (profileResponse.getStatusCode() == HttpStatus.OK) {
                    OurUsers userProfile = profileResponse.getBody();

                    // Create a response map to include token and userData
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("token", response.getToken());
                    responseMap.put("userData", userProfile);
                    System.out.println("ookdskdskm"+responseMap);

                    return ResponseEntity.ok().body(responseMap);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }

            return ResponseEntity.status(response.getStatusCode()).body(null);
        }



    @PostMapping("/signup/userSignup")
    public ResponseEntity<ReqRes> signUpAsUser(@RequestBody ReqRes registrationRequest) {
        ReqRes response = authService.signUpAsUser(registrationRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/signup/adminSignup")
    public ResponseEntity<ReqRes> signUpAsAdmin(@RequestBody ReqRes registrationRequest) {
        ReqRes response = authService.signUpAsAdmin(registrationRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/signup/employeeSignup")
    public ResponseEntity<ReqRes> signUpAsEmployee(@RequestBody ReqRes registrationRequest) {
        ReqRes response = authService.signUpAsEmployee(registrationRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ReqRes logout(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.substring(7); // Remove "Bearer " prefix
        return authService.logout(tokenWithoutBearer);
    }

    @PostMapping("/changepassword")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ReqRes> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        String username = authentication.getName();
        ReqRes response = authService.changePassword(username, changePasswordRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/qrSignin/{phoneNumber}")
    public ResponseEntity<byte[]> generateQRCodeForSignIn(@PathVariable String phoneNumber) {
        try {
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(phoneNumber);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeImage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Object> userinfo(){

        return ResponseEntity.ok("logged in successfully");
    }

    @PostMapping("/qrcode/validate")
    public ResponseEntity<Boolean> validateAndFetchUserInfo(@RequestBody byte[] qrCodeBytes) {
        // Decode QR code and validate
        QRCodeGenerator.DecodedQRCode decodedQRCode = QRCodeGenerator.decodeQRCode(qrCodeBytes);
        if (decodedQRCode != null && decodedQRCode.isValid()) {
            // Extract the username from the decoded QR code
            String username = decodedQRCode.getUsername();

            // Construct the API endpoint URL to fetch user information
            String userInfoApiUrl = "https://http:localhost:8080/auth/userinfo/" + username; // Replace with actual API endpoint

            // Make a GET request to fetch user information
            ResponseEntity<OurUsers> userInfoResponse = restTemplate.getForEntity(userInfoApiUrl, OurUsers.class);

            // Check the response status
            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                OurUsers userInfo = userInfoResponse.getBody();
                // Example: Print user information (replace with your logic)
                System.out.println("Username: " + userInfo.getUsername() + ", Phone Number: " + userInfo.getPhoneNumber());
                return ResponseEntity.ok(true);
            } else {
                // Handle other response statuses as needed
                return ResponseEntity.ok(false);
            }
        } else {
            // Invalid QR code or decoding failed
            return ResponseEntity.ok(false);
        }
    }
    @GetMapping("/checkPhoneNumberExists/{phoneNumber}")
    public ResponseEntity<Void> checkPhoneNumberExists(@PathVariable String phoneNumber) {
        boolean exists = ourUserRepo.existsByPhoneNumber(phoneNumber);
        if (exists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
