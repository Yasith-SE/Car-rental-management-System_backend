package edu.icet.model.dto;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CustomerDto {

    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String address;
    private Integer postalCode;

}