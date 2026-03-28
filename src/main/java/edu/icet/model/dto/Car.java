package edu.icet.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile; // Add this import

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Car {
    private Long id;
    private String name;
    private String year;
    private Double price;
    private String image; // This stores the URL string for the database
    private MultipartFile imageFile; // This catches the actual file from React
}