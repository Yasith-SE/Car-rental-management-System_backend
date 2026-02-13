package edu.icet.controller;

import edu.icet.model.dto.AdminDto;
import edu.icet.service.AdminSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/adminSignUp")
@CrossOrigin(origins = "")
@RequiredArgsConstructor
public class AdminSignInController {

    private final AdminSignInService service;

    @PostMapping("/addAdmin")
    public Map<String,String> addAdmin(@RequestBody AdminDto adminDto) {

        service.addAdmin(adminDto);
        return Map.of("status", "success", "message", "Admin registered successfully!");
    }
}