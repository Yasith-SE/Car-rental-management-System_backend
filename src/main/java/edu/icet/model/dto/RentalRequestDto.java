package edu.icet.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalRequestDto {
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
