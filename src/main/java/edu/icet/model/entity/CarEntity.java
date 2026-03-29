package edu.icet.model.entity;

import jakarta.persistence.*; // ADD THIS LINE
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String year;
    private Double price;
    private String image;

}