package edu.icet.controller;

import edu.icet.model.dto.AdminDto;
import edu.icet.service.AdminSignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/adminLogin")
@CrossOrigin(origins = "")
public class AdminSignInController {
    @Autowired
    private AdminSignInService service;

    @PostMapping("/addAdmin")
    public ResponseEntity<?> addAdmin(@RequestBody AdminDto adminDto) {
        service.addAdmin(adminDto);
        return ResponseEntity.ok(Map.of("message", "Admin Registered"));
    }
}