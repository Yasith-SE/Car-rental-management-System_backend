package edu.icet.model.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CarListing {

    private Long id;
    private String name;
    private String year;
    private Double price;
    private String image;

}