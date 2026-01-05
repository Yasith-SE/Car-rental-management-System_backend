package edu.icet.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDate;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
public class AdminEntity {

    @Id
    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String address;
    private int postalCode;

}
