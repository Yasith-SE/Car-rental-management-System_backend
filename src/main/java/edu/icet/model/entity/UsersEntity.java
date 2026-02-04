package edu.icet.model.entity;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")

public class UsersEntity {

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


}
