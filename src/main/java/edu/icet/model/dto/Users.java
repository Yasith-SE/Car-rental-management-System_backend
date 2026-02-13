package edu.icet.model.dto;
import lombok.Data;

@Data
public class Users {
    private Long id;
    private String name;
    private String year;
    private Double price;
    private String image;
}