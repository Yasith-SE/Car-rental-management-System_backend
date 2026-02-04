package edu.icet.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor

@AllArgsConstructor
@ToString(exclude = "password"  )
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate dateOfBirth;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


    private String address;

    private int postalCode;

}
