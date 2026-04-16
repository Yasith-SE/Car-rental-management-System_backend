package edu.icet.controller;

import edu.icet.model.dto.AccessStatusUpdateDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.LoginAudit;
import edu.icet.model.entity.User;
import edu.icet.repository.LoginAuditRepository;
import edu.icet.repository.RentalRequestRepository;
import edu.icet.repository.UserRepository;
import edu.icet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LoginAuditRepository loginAuditRepository;
    private final RentalRequestRepository rentalRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/login-history")
    public ResponseEntity<List<LoginAudit>> getLoginHistory() {
        return ResponseEntity.ok(loginAuditRepository.findAll(Sort.by(Sort.Direction.DESC, "time")));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalLogins", loginAuditRepository.count());
        stats.put("activeRentals", rentalRequestRepository.countByStatusIn(Set.of("PENDING", "CONFIRMED", "ACTIVE")));
        stats.put("pendingApprovals", userRepository.countByAccessStatus("PENDING_APPROVAL"));
        stats.put("approvedCustomers", userRepository.countByRoleAndAccessStatus("CUSTOMER", "APPROVED"));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto, Authentication authentication) {
        try {
            User actor = currentUser(authentication);
            UserDto createdUser = userService.registerUser(userDto, actor);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User created successfully.",
                    "user", createdUser
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam("role") String role,
            Authentication authentication
    ) {
        try {
            UserDto updatedUser = userService.updateUserRole(id, role, currentUser(authentication));
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/users/{id}/access")
    public ResponseEntity<?> updateUserAccess(
            @PathVariable Long id,
            @RequestBody AccessStatusUpdateDto accessStatusUpdateDto,
            Authentication authentication
    ) {
        try {
            UserDto updatedUser = userService.updateUserAccessStatus(
                    id,
                    accessStatusUpdateDto.getStatus(),
                    currentUser(authentication)
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Access updated successfully.",
                    "user", updatedUser
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    private User currentUser(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof User user ? user : null;
    }
}
