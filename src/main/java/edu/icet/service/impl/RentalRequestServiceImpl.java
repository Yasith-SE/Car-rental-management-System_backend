package edu.icet.service.impl;

import edu.icet.model.dto.RentalRequestDto;
import edu.icet.model.entity.RentalRequestEntity;
import edu.icet.model.entity.User;
import edu.icet.repository.RentalRequestRepository;
import edu.icet.service.RentalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalRequestServiceImpl implements RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;

    @Override
    public RentalRequestDto createRentalRequest(RentalRequestDto rentalRequestDto, User requester) {
        if (requester == null) {
            throw new RuntimeException("You must be logged in to create a rental request.");
        }

        if (!"APPROVED".equalsIgnoreCase(requester.getAccessStatus())) {
            throw new RuntimeException("This account is not approved for rental requests.");
        }

        RentalRequestEntity entity = new RentalRequestEntity();
        entity.setCarId(rentalRequestDto.getCarId());
        entity.setUserId(requester.getId());
        entity.setCarName(rentalRequestDto.getCarName());
        entity.setCustomerName(requester.getName());
        entity.setPickupLocation(rentalRequestDto.getPickupLocation());
        entity.setDropoffLocation(rentalRequestDto.getDropoffLocation());
        entity.setPickupPlaceId(rentalRequestDto.getPickupPlaceId());
        entity.setDropoffPlaceId(rentalRequestDto.getDropoffPlaceId());
        entity.setDestinationPlan(rentalRequestDto.getDestinationPlan());
        entity.setPurposeCategory(rentalRequestDto.getPurposeCategory());
        entity.setPurposeDetails(rentalRequestDto.getPurposeDetails());
        entity.setEstimatedDistanceKm(rentalRequestDto.getEstimatedDistanceKm());
        entity.setRentalDays(rentalRequestDto.getRentalDays());
        entity.setApproximateMonths(rentalRequestDto.getApproximateMonths());
        entity.setQuotedBaseDailyRate(rentalRequestDto.getQuotedBaseDailyRate());
        entity.setQuotedAdjustedDailyRate(rentalRequestDto.getQuotedAdjustedDailyRate());
        entity.setEstimatedTotal(rentalRequestDto.getEstimatedTotal());
        entity.setPurposeLabel(rentalRequestDto.getPurposeLabel());
        entity.setDurationRateLabel(rentalRequestDto.getDurationRateLabel());
        entity.setDistanceRateLabel(rentalRequestDto.getDistanceRateLabel());
        entity.setRouteDistanceSource(rentalRequestDto.getRouteDistanceSource());
        entity.setGoogleRouteDistanceKm(rentalRequestDto.getGoogleRouteDistanceKm());
        entity.setGoogleRouteDurationText(rentalRequestDto.getGoogleRouteDurationText());
        entity.setStartDate(rentalRequestDto.getStartDate());
        entity.setEndDate(rentalRequestDto.getEndDate());
        entity.setStatus("PENDING");
        entity.setCreatedAt(LocalDateTime.now());

        return mapToDto(rentalRequestRepository.save(entity));
    }

    private RentalRequestDto mapToDto(RentalRequestEntity entity) {
        RentalRequestDto dto = new RentalRequestDto();
        dto.setId(entity.getId());
        dto.setCarId(entity.getCarId());
        dto.setUserId(entity.getUserId());
        dto.setCarName(entity.getCarName());
        dto.setCustomerName(entity.getCustomerName());
        dto.setPickupLocation(entity.getPickupLocation());
        dto.setDropoffLocation(entity.getDropoffLocation());
        dto.setPickupPlaceId(entity.getPickupPlaceId());
        dto.setDropoffPlaceId(entity.getDropoffPlaceId());
        dto.setDestinationPlan(entity.getDestinationPlan());
        dto.setPurposeCategory(entity.getPurposeCategory());
        dto.setPurposeDetails(entity.getPurposeDetails());
        dto.setEstimatedDistanceKm(entity.getEstimatedDistanceKm());
        dto.setRentalDays(entity.getRentalDays());
        dto.setApproximateMonths(entity.getApproximateMonths());
        dto.setQuotedBaseDailyRate(entity.getQuotedBaseDailyRate());
        dto.setQuotedAdjustedDailyRate(entity.getQuotedAdjustedDailyRate());
        dto.setEstimatedTotal(entity.getEstimatedTotal());
        dto.setPurposeLabel(entity.getPurposeLabel());
        dto.setDurationRateLabel(entity.getDurationRateLabel());
        dto.setDistanceRateLabel(entity.getDistanceRateLabel());
        dto.setRouteDistanceSource(entity.getRouteDistanceSource());
        dto.setGoogleRouteDistanceKm(entity.getGoogleRouteDistanceKm());
        dto.setGoogleRouteDurationText(entity.getGoogleRouteDurationText());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
