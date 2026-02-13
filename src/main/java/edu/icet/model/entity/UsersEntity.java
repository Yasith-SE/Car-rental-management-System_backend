package edu.icet.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "available_cars")
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String year;
    private Double price;
    private String image;


}
