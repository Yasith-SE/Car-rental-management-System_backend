package edu.icet.controller;


import edu.icet.model.dto.AdminDto;
import edu.icet.repository.AdminSignInRepository;
import edu.icet.service.AdminSignInService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void addAdmin(@Valid @RequestBody AdminDto adminDto){
        adminSignInService.addAdmin(adminDto);
    }


}
