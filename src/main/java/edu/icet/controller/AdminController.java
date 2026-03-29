package edu.icet.controller;

import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.LoginAudit;
import edu.icet.repository.LoginAuditRepository;
import edu.icet.repository.UserRepository;
import edu.icet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {

    private final LoginAuditRepository loginAuditRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    // Fetch all logs, sorted by newest first
    @GetMapping("/login-history")
    public ResponseEntity<List<LoginAudit>> getLoginHistory() {
        return ResponseEntity.ok(loginAuditRepository.findAll(Sort.by(Sort.Direction.DESC, "time")));
    }

    // Fetch dashboard numbers
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalLogins", loginAuditRepository.count());
        stats.put("activeRentals", 0L); // We will update this when Rentals are built
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/users") // This becomes /api/admin/users
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestParam("role") String role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}