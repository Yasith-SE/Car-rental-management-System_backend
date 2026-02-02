package edu.icet.model.entity;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")

public class UsersEntity {

    @
    private String email;


    private String password;


}
