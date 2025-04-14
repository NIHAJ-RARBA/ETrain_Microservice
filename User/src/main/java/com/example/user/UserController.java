package com.example.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.OtpGenerationRequest;
import com.example.user.dto.OtpValidationRequest;
import com.example.user.dto.UserRegistrationRequest;

@RestController
public class UserController {

    private final UserService userService;
    private final RestTemplate restTemplate;
    // private static final String OTP_SERVICE_URL =
    // "http://otp-service:${OTP_SERVICE_PORT:80}";
    private static final String OTP_SERVICE_URL = "http://otp-service:8080";

    public UserController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> createUser(@RequestBody UserRegistrationRequest request) {
        // User pendingUser = userService.createPendingUser(request);

        try {
            userService.createUser(request.getName(), request.getEmail(), request.getPassword());

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }

        OtpGenerationRequest otpRequest = new OtpGenerationRequest(request.getEmail());

        restTemplate.postForEntity(OTP_SERVICE_URL + "/generate", otpRequest, String.class);

        return ResponseEntity.ok("OTP sent to mail");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpValidationRequest request) {

        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                OTP_SERVICE_URL + "/validate",
                request,
                Boolean.class);

        if (response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody())) {
            // userService.activateUser(request.getEmail());

            return ResponseEntity.ok("User activated successfully");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody LoginRequest loginRequest) {

        if (userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword())) {

            return ResponseEntity.ok("User login successful");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email or password");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);

        if (user != null) {

            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        if (user != null) {

            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
