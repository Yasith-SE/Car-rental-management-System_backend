package edu.icet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String year;
    private Double price;
    private String category;
    private String image;
    private String modelUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String galleryImages;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String showroomSummary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String supportPromptTemplate;

    @Lob
    @Column(columnDefinition = "TEXT")
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
