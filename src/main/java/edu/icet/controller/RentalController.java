package edu.icet.controller;

import edu.icet.model.dto.RentalRequestDto;
import edu.icet.model.entity.User;
import edu.icet.service.RentalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalRequestService rentalRequestService;

    @PostMapping
    public ResponseEntity<?> createRentalRequest(
            @RequestBody RentalRequestDto rentalRequestDto,
            Authentication authentication
    ) {
        try {
            User requester = currentUser(authentication);
            return new ResponseEntity<>(
                    rentalRequestService.createRentalRequest(rentalRequestDto, requester),
                    HttpStatus.CREATED
            );
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    private User currentUser(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof User user ? user : null;
    }
}
