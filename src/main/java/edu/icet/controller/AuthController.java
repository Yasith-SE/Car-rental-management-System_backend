package edu.icet.controller;

import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.LoginAudit;
import edu.icet.model.entity.User;
import edu.icet.repository.LoginAuditRepository;
import edu.icet.repository.UserRepository;
import edu.icet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173/") // Critical for React to connect
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LoginAuditRepository loginAuditRepository;
    private final UserRepository userRepository;
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
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {
            UserDto loggedInUser = userService.loginUser(loginDto);

            // --- CREATE AUDIT LOG ON SUCCESSFUL LOGIN ---
            LoginAudit audit = new LoginAudit();
            audit.setName(loggedInUser.getName());
            audit.setTime(LocalDateTime.now());
            audit.setIp(request.getRemoteAddr()); // Gets network IP

            // Extracts browser/device info and limits it to 50 chars to keep the table clean
            String userAgent = request.getHeader("User-Agent");
            audit.setDevice(userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) : "Unknown Device");

            audit.setType(loggedInUser.getRole() + " LOGIN");
            loginAuditRepository.save(audit);
            // --------------------------------------------

            return ResponseEntity.ok(loggedInUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // --- NEW ENDPOINT TO UPLOAD PROFILE IMAGE ---
    @PostMapping("/update-profile-image")
    public ResponseEntity<String> updateProfileImage(
            @RequestParam("userId") Long userId,
            @RequestParam("imageFile") MultipartFile imageFile) {

        try {
            // 1. Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Setup the directory to save user photos
            String uploadDir = "src/main/resources/static/uploads/users/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            // 3. Create a unique filename and save it
            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath);

            // 4. Update the user's image path in the database
            String imageUrl = "http://localhost:8080/uploads/users/" + fileName;
            user.setImage(imageUrl);
            userRepository.save(user);

            // 5. Send the new image URL back to React
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save image.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unknown error occurred.");
        }
    }


}