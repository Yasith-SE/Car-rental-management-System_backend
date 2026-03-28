package edu.icet.controller;

import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173/") // Critical for React to connect
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.registerUser(userDto);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Return 400 Bad Request if email already exists
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        try {
            UserDto loggedInUser = userService.loginUser(loginDto);
            return ResponseEntity.ok(loggedInUser); // Sends the user data back to React
        } catch (RuntimeException e) {
            // Return 401 Unauthorized if password/email is wrong
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // In a real app, you would verify if the requester is an ADMIN here
        return ResponseEntity.ok(userService.getAllUsers());
    }
}