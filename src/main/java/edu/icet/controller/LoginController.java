package edu.icet.controller;


import edu.icet.model.dto.LoginUsers;
import edu.icet.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/userLogin")
@CrossOrigin(origins = "")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/addUser")
    public Map<String,String>userLogin(LoginUsers loginUsers){

        String userRole = loginService.loginUser(loginUsers);
        if(userRole != null){
            return Map.of("status", "success","role", userRole,"message", "Login Successful");

        }else{

            return Map.of("status","error","message","Invalid email");
        }

    }

}
