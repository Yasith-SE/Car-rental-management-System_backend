package edu.icet.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
public class CustomerEntity {
    @Id
    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String address;
    private int postalCode;

}
