package edu.icet.model.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class EmployeeDto {

    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String address;
    private int postalCode;
}
