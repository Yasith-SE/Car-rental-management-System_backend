package edu.icet.controller;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.dto.CarListing;
import edu.icet.service.CustomerSignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerSigninController {
    @Autowired
    private CustomerSignInService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CustomerDto dto) {
        service.registerCustomer(dto);
        return ResponseEntity.ok(Map.of("message", "Success"));
    }

    @GetMapping("/available-cars")
    public List<CarListing> getCars() {
        return service.getAllAvailableCars();
    }
}