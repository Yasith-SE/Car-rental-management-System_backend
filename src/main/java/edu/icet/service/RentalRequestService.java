package edu.icet.service;

import edu.icet.model.dto.RentalRequestDto;
import edu.icet.model.entity.User;

import java.util.List;

public interface RentalRequestService {
    RentalRequestDto createRentalRequest(RentalRequestDto rentalRequestDto, User requester);

    List<RentalRequestDto> getAllRentalRequests();

    RentalRequestDto updateRentalStatus(Long id, String status, User actor);
}
