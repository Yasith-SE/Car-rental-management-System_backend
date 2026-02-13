package edu.icet.service.Impl;


import edu.icet.model.dto.CustomerDto;
import edu.icet.repository.CustomerSignInRepository;
import edu.icet.service.CustomerSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


@Service
@RequiredArgsConstructor
public class CustomerSignInServicesImpl implements CustomerSignInService {

    private final CustomerSignInRepository customerSignInRepository;

    @Override
    public void registerCustomer(CustomerDto customerDto) {
        try {
            customerSignInRepository.save(customerDto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid Runtime error"+e.getMessage());
        }
    }
}