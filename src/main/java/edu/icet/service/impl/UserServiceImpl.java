package edu.icet.service.impl;

import edu.icet.model.dto.AuthResponseDto;
import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.User;
import edu.icet.repository.UserRepository;
import edu.icet.security.JwtService;
import edu.icet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_PENDING = "PENDING_APPROVAL";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_SUSPENDED = "SUSPENDED";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Override
    public UserDto registerUser(UserDto userDto, User creator) {
        String email = normalizeEmail(userDto.getEmail());

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new RuntimeException("An account with this email already exists.");
        }

        validateUserInput(userDto);

        boolean createdByAdmin = isAdmin(creator);
        String role = normalizeRole(userDto.getRole());
        String accessStatus = role.equals(ROLE_ADMIN) || createdByAdmin
                ? STATUS_APPROVED
                : STATUS_PENDING;
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName(trim(userDto.getName()));
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAddress(trim(userDto.getAddress()));
        user.setPostalCode(trim(userDto.getPostalCode()));
        user.setRole(role);
        user.setPhone(trim(userDto.getPhone()));
        user.setLicenseNumber(trim(userDto.getLicenseNumber()));
        user.setNotes(trim(userDto.getNotes()));
        user.setTitle(resolveTitle(role, userDto.getTitle()));
        user.setAccountSource(
                createdByAdmin
                        ? "ADMIN_CREATED"
                        : role.equals(ROLE_ADMIN)
                        ? "PUBLIC_ADMIN_SIGNUP"
                        : "PUBLIC_SIGNUP"
        );
        user.setAccessStatus(accessStatus);
        user.setCreatedAt(now);
        user.setCreatedBy(
                createdByAdmin
                        ? creator.getName()
                        : role.equals(ROLE_ADMIN)
                        ? "Public admin registration form"
                        : "Public registration form"
        );

        if (STATUS_APPROVED.equals(accessStatus)) {
            user.setApprovedAt(now);
            user.setReviewedAt(now);
            user.setApprovedBy(
                    createdByAdmin
                            ? creator.getName()
                            : role.equals(ROLE_ADMIN)
                            ? "Automatic admin activation"
                            : ""
            );
            user.setReviewedBy(
                    createdByAdmin
                            ? creator.getName()
                            : role.equals(ROLE_ADMIN)
                            ? "Automatic admin activation"
                            : ""
            );
        }

        return toDto(userRepository.save(user));
    }

    @Override
    public AuthResponseDto loginUser(LoginDto loginDto) {
        String email = normalizeEmail(loginDto.getEmail());
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        String rawPassword = loginDto.getPassword() == null ? "" : loginDto.getPassword();
        String storedPassword = user.getPassword() == null ? "" : user.getPassword();

        if (!passwordEncoder.matches(rawPassword, storedPassword)) {
            if (rawPassword.equals(storedPassword)) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid email or password.");
            }
        }

        String accessStatus = normalizeStatus(user.getAccessStatus());

        if (!STATUS_APPROVED.equals(accessStatus)) {
            throw new RuntimeException(resolveAccessMessage(accessStatus));
        }

        user.setAccessStatus(accessStatus);
        User savedUser = userRepository.save(user);

        return new AuthResponseDto(jwtService.generateToken(savedUser), toDto(savedUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found.");
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUserRole(Long id, String newRole, User actor) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        String normalizedRole = normalizeRole(newRole);
        user.setRole(normalizedRole);
        user.setTitle(resolveTitle(normalizedRole, user.getTitle()));

        if (ROLE_ADMIN.equals(normalizedRole)) {
            user.setAccessStatus(STATUS_APPROVED);
            user.setApprovedAt(LocalDateTime.now());
            user.setApprovedBy(actor != null ? actor.getName() : "Admin role update");
        } else if (isBlank(user.getAccessStatus())) {
            user.setAccessStatus(STATUS_APPROVED);
        }

        user.setReviewedAt(LocalDateTime.now());
        user.setReviewedBy(actor != null ? actor.getName() : "Admin role update");

        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUserAccessStatus(Long id, String nextStatus, User actor) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        String normalizedStatus = normalizeStatus(nextStatus);

        if (ROLE_ADMIN.equalsIgnoreCase(user.getRole()) && !STATUS_APPROVED.equals(normalizedStatus)) {
            throw new RuntimeException(
                    "Admin and employee accounts must remain approved. Change the role first if customer controls are needed."
            );
        }

        user.setAccessStatus(normalizedStatus);
        user.setReviewedAt(LocalDateTime.now());
        user.setReviewedBy(actor != null ? actor.getName() : "Admin review");

        if (STATUS_APPROVED.equals(normalizedStatus)) {
            user.setApprovedAt(LocalDateTime.now());
            user.setApprovedBy(actor != null ? actor.getName() : "Admin review");
        }

        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateProfileImage(Long id, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new RuntimeException("Profile image file is required.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        try {
            Path uploadDirectory = Paths.get("src/main/resources/static/uploads/users");
            Files.createDirectories(uploadDirectory);

            String originalFileName = imageFile.getOriginalFilename() == null
                    ? "profile-image"
                    : imageFile.getOriginalFilename();
            String cleanFileName = sanitizeFileName(originalFileName);
            String fileName = "user_" + id + "_" + System.currentTimeMillis() + "_" + cleanFileName;
            Path filePath = uploadDirectory.resolve(fileName);

            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            user.setImage(buildAssetUrl("uploads/users/" + fileName));
            return toDto(userRepository.save(user));
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save the profile image.");
        }
    }

    @Override
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPostalCode(user.getPostalCode());
        dto.setRole(normalizeRole(user.getRole()));
        dto.setImage(trim(user.getImage()));
        dto.setPhone(trim(user.getPhone()));
        dto.setLicenseNumber(trim(user.getLicenseNumber()));
        dto.setNotes(trim(user.getNotes()));
        dto.setTitle(resolveTitle(user.getRole(), user.getTitle()));
        dto.setAccountSource(
                isBlank(user.getAccountSource()) ? "BACKEND" : user.getAccountSource().trim()
        );
        dto.setAccessStatus(normalizeStatus(user.getAccessStatus()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setCreatedBy(trim(user.getCreatedBy()));
        dto.setApprovedAt(user.getApprovedAt());
        dto.setApprovedBy(trim(user.getApprovedBy()));
        dto.setReviewedAt(user.getReviewedAt());
        dto.setReviewedBy(trim(user.getReviewedBy()));
        return dto;
    }

    private void validateUserInput(UserDto userDto) {
        if (isBlank(userDto.getName()) || isBlank(userDto.getEmail()) || isBlank(userDto.getPassword())) {
            throw new RuntimeException("Name, email, and password are required.");
        }

        if (userDto.getPassword().trim().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long.");
        }
    }

    private String resolveTitle(String role, String providedTitle) {
        if (!isBlank(providedTitle)) {
            return providedTitle.trim();
        }

        return ROLE_ADMIN.equals(normalizeRole(role))
                ? "Employee Access Account"
                : "Customer Rental Account";
    }

    private String resolveAccessMessage(String accessStatus) {
        return switch (accessStatus) {
            case STATUS_PENDING -> "This account is waiting for admin approval.";
            case STATUS_REJECTED -> "This account was rejected. Please contact the showroom staff.";
            case STATUS_SUSPENDED -> "This account is paused. Please contact the showroom staff.";
            default -> "This account cannot sign in right now.";
        };
    }

    private boolean isAdmin(User user) {
        return user != null && ROLE_ADMIN.equalsIgnoreCase(user.getRole());
    }

    private String normalizeEmail(String email) {
        return trim(email).toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        return ROLE_ADMIN.equalsIgnoreCase(trim(role)) ? ROLE_ADMIN : ROLE_CUSTOMER;
    }

    private String normalizeStatus(String accessStatus) {
        String normalized = trim(accessStatus).toUpperCase(Locale.ROOT);

        if (STATUS_PENDING.equals(normalized)
                || STATUS_APPROVED.equals(normalized)
                || STATUS_REJECTED.equals(normalized)
                || STATUS_SUSPENDED.equals(normalized)) {
            return normalized;
        }

        return STATUS_APPROVED;
    }

    private String sanitizeFileName(String fileName) {
        return trim(fileName).replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    private String buildAssetUrl(String relativePath) {
        return trim(appBaseUrl).replaceAll("/+$", "") + "/" + relativePath.replace("\\", "/");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return trim(value).isEmpty();
    }
}
