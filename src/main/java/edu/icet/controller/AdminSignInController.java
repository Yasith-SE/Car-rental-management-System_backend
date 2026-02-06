package edu.icet.controller;


import edu.icet.model.dto.AdminDto;
import edu.icet.model.dto.Users;
import edu.icet.repository.AdminSignInRepository;
import edu.icet.service.AdminSignInService;
import io.micrometer.observation.Observation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/adminLogin")
@RestController
public class AdminSignInController {

    @Autowired
    private AdminSignInService adminSignInService;

    @GetMapping("/allAdmin")
    public List<AdminDto> getAll(){

        return adminSignInService.getAllAdmin();

    }

    @PostMapping("/addAdmin")
    public ResponseEntity<?> addAdmin(@Valid @RequestBody AdminDto adminDto){

        adminSignInService.addAdmin(adminDto);
        return ResponseEntity.ok(Map.of("message","Admin login success"));

    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> user(@Valid @RequestBody Users adminUser){

        boolean isValid = adminSignInService.authentication(adminUser);

        if(isValid){
            return ResponseEntity.ok(Map.of("message","userLogin success"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email"));

    }


}
