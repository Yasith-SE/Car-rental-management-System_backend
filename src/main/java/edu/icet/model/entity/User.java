package edu.icet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String address;
    private String postalCode;
    private String role;
    private String image;
    private String phone;
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
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
