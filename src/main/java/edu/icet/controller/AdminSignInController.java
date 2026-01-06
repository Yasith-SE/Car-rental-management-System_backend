package edu.icet.controller;


import edu.icet.model.dto.AdminDto;
import edu.icet.repository.AdminSignInRepository;
import edu.icet.service.AdminSignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminLogin")
public class AdminSignInController {


    AdminSignInService adminSignInService = new AdminSignInService();

    @GetMapping("/allAdmin")
    public List<AdminDto> getAll(){
        return adminSignInService.getAllAdmin();

    }

    @PostMapping("/addAdmin")
    public void addAdmin(AdminDto adminDto){
        adminSignInService.addAdmin(adminDto);
    }


}
