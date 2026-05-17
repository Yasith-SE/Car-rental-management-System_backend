package edu.icet.service.impl;

import edu.icet.model.dto.RentalRequestDto;
import edu.icet.model.entity.NotificationEntity;
import edu.icet.model.entity.RentalRequestEntity;
import edu.icet.model.entity.User;
import edu.icet.repository.NotificationRepository;
import edu.icet.repository.RentalRequestRepository;
import edu.icet.service.RentalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RentalRequestServiceImpl implements RentalRequestService {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "PENDING",
            "CONFIRMED",
            "ACTIVE",
            "COMPLETED",
            "CANCELLED"
    );

    private final RentalRequestRepository rentalRequestRepository;
    private final NotificationRepository notificationRepository;

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

    @Override
    public List<RentalRequestDto> getAllRentalRequests() {
        return rentalRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public RentalRequestDto updateRentalStatus(Long id, String status, User actor) {
        if (actor == null || !"ADMIN".equalsIgnoreCase(actor.getRole())) {
            throw new RuntimeException("Only admin accounts can update rental requests.");
        }

        String normalizedStatus = normalizeStatus(status);
        RentalRequestEntity entity = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental request not found."));

        entity.setStatus(normalizedStatus);
        RentalRequestEntity savedEntity = rentalRequestRepository.save(entity);
        createCustomerStatusNotification(savedEntity, normalizedStatus);
        return mapToDto(savedEntity);
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

    private String normalizeStatus(String status) {
        String normalizedStatus = String.valueOf(status == null ? "" : status)
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            throw new RuntimeException("Unsupported rental status.");
        }

        return normalizedStatus;
    }

    private void createCustomerStatusNotification(RentalRequestEntity rentalRequest, String status) {
        if (rentalRequest.getUserId() == null) {
            return;
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(rentalRequest.getUserId());
        notification.setRentalRequestId(rentalRequest.getId());
        notification.setType("success");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        String carName = trim(rentalRequest.getCarName()).isEmpty()
                ? "your selected vehicle"
                : rentalRequest.getCarName();

        if ("CONFIRMED".equals(status)) {
            notification.setTitle("Rental request approved");
            notification.setMessage("Your request for " + carName + " has been approved by the showroom manager.");
            notificationRepository.save(notification);
            return;
        }

        if ("CANCELLED".equals(status)) {
            notification.setType("warning");
            notification.setTitle("Your request is cancelled");
            notification.setMessage("Your request for " + carName + " was cancelled by the showroom manager.");
            notificationRepository.save(notification);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
