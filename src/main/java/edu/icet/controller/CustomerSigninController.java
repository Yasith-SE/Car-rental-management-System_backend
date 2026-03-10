package edu.icet.controller;

import edu.icet.model.dto.CustomerDto;
import edu.icet.service.CustomerSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customerSignUp")
@CrossOrigin(origins = "http://localhost:5173/")
@RequiredArgsConstructor
public class CustomerSigninController {


    private final CustomerSignInService service;

    @PostMapping("/customerAdd")
    public Map<String, String> registerCustomer(@RequestBody CustomerDto customerDto) {

        service.registerCustomer(customerDto);

        return Map.of("message", "Success");
    }


}