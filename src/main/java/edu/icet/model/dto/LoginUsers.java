package edu.icet.model.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class LoginUsers {

    private String email;
    private String password;
    private String role;


}
