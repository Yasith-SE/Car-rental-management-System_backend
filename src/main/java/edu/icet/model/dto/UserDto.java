package edu.icet.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UserDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String address;
    private String postalCode;
    private String role;
}