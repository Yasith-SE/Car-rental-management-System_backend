package edu.icet.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String address;
    private String postalCode;
    private String role;
    private String image;
    private MultipartFile imageFile;
    private String phone;
    private String licenseNumber;
    private String notes;
    private String title;
    private String accountSource;
    private String accessStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
}
