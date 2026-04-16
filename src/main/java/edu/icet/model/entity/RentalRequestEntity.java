package edu.icet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_requests")
public class RentalRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carId;
    private Long userId;
    private String carName;
    private String customerName;
    private String pickupLocation;
    private String dropoffLocation;
    private String pickupPlaceId;
    private String dropoffPlaceId;
    private String destinationPlan;
    private String purposeCategory;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String purposeDetails;

    private Double estimatedDistanceKm;
    private Integer rentalDays;
    private Double approximateMonths;
    private Double quotedBaseDailyRate;
    private Double quotedAdjustedDailyRate;
    private Double estimatedTotal;
    private String purposeLabel;
    private String durationRateLabel;
    private String distanceRateLabel;
    private String routeDistanceSource;
    private Double googleRouteDistanceKm;
    private String googleRouteDurationText;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
}
