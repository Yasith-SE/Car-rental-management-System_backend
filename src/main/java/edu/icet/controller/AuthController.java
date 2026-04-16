package edu.icet.controller;

import edu.icet.model.dto.AuthResponseDto;
import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.LoginAudit;
import edu.icet.model.entity.User;
import edu.icet.repository.LoginAuditRepository;
import edu.icet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LoginAuditRepository loginAuditRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto, Authentication authentication) {
        try {
            User actor = currentUser(authentication);
            UserDto createdUser = userService.registerUser(userDto, actor);

            String message;
            if ("ADMIN".equalsIgnoreCase(createdUser.getRole())) {
                message = "Admin / employee account registered successfully with full access.";
            } else if (actor != null && "ADMIN".equalsIgnoreCase(actor.getRole())) {
                message = "Customer account created successfully.";
            } else {
                message = "Registration submitted. Wait for admin approval before logging in.";
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", message,
                    "user", createdUser
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {
            AuthResponseDto authResponse = userService.loginUser(loginDto);
            UserDto loggedInUser = authResponse.getUser();

            LoginAudit audit = new LoginAudit();
            audit.setName(loggedInUser.getName());
            audit.setTime(LocalDateTime.now());
            audit.setIp(request.getRemoteAddr());

            String userAgent = request.getHeader("User-Agent");
            audit.setDevice(
                    userAgent != null
                            ? userAgent.substring(0, Math.min(userAgent.length(), 120))
                            : "Unknown Device"
            );
            audit.setType(loggedInUser.getRole() + " LOGIN");
            loginAuditRepository.save(audit);

            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User actor = currentUser(authentication);

        if (actor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized request."));
        }

        return ResponseEntity.ok(Map.of("user", userService.toDto(actor)));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id, Authentication authentication) {
        User actor = currentUser(authentication);

        if (actor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized request."));
        }

        if (!canAccessProfile(actor, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You cannot view this profile."));
        }

        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    @PostMapping("/profile-image")
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("userId") Long userId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Authentication authentication
    ) {
        User actor = currentUser(authentication);

        if (actor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized request."));
        }

        if (!canAccessProfile(actor, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You cannot update this profile."));
        }

        try {
            UserDto updatedUser = userService.updateProfileImage(userId, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Profile image updated successfully.",
                    "imageUrl", updatedUser.getImage(),
                    "user", updatedUser
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    private User currentUser(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof User user ? user : null;
    }

    private boolean canAccessProfile(User actor, Long targetUserId) {
        return actor != null
                && ("ADMIN".equalsIgnoreCase(actor.getRole()) || actor.getId().equals(targetUserId));
    }
}
