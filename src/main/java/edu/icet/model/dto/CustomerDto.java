package edu.icet.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class CustomerDto {
    private Long id;

    @NotBlank(message = "Enter your name")
    private String name;

    @Past(message = "Date of birth should enter")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Enter your correct gmail address")
    @Email(message = "Gmail must be valid")
    private String email;

    @NotBlank(message = "Enter your password")
    @Size(min = 10, message = "Password should be minimum 10 characters")
    private String password;

    @NotBlank(message = "Enter your living address")
    private String address;

    @Min(value = 1000, message = "Enter your postal code ")
    private int postalCode;

}
