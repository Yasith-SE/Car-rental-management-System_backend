package edu.icet.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    private Long id;
    private String name;
    private String year;
    private Double price;
    private String category;
    private String image;
    private MultipartFile imageFile;
    private String modelUrl;
    private MultipartFile modelFile;
    private List<MultipartFile> galleryFiles;
    private String galleryImages;
    private String showroomSummary;
    private String supportPromptTemplate;
    private String partHighlights;
    private Double horsepower;
    private Double topSpeed;
    private Double zeroToSixty;
    private Double quarterMile;
    private Double brakePower;
    private Double brakeResponse;
    private LocalDateTime createdAt;
    private String createdBy;
}
