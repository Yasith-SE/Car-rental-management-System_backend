package edu.icet.controller;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.dto.Users;
import edu.icet.service.CustomerSignInServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/customer-login")
@RestController
public class CustomerSigninController {


    @Autowired
    private CustomerSignInServices service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CustomerDto customerDto) {

        service.addCustomer(customerDto);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));

    }
    @PostMapping("/customer-login")
    public ResponseEntity<?> login(@Valid @RequestBody Users loginRequest) {

        boolean isValid = service.authenticate(loginRequest);

            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "Login successful"));

            }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));

    }



}