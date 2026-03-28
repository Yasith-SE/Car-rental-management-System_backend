package edu.icet.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth; // Maps to your type="date" input

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String address;
    private String postalCode; // String is safer for postal codes than Integer

    private String role; // e.g., "CUSTOMER" or "ADMIN"
}