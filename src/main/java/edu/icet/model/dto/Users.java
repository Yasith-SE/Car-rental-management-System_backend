package edu.icet.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Users {

    @Email(message = "Enter your correct email")
    private String email;


    @NotBlank(message = "enter is required")
    private String password;
}
