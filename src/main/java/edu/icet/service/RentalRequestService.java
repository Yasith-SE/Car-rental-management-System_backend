package edu.icet.service;

import edu.icet.model.dto.RentalRequestDto;
import edu.icet.model.entity.User;

public interface RentalRequestService {
    RentalRequestDto createRentalRequest(RentalRequestDto rentalRequestDto, User requester);
}
