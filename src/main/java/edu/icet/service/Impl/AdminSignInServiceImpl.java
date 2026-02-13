package edu.icet.service.Impl;

import edu.icet.model.dto.AdminDto;
import edu.icet.repository.AdminSignInRepository;
import edu.icet.service.AdminSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class AdminSignInServiceImpl implements AdminSignInService {

    private final AdminSignInRepository adminSignInRepository;

    @Override
    public void addAdmin(AdminDto adminDto) {
        try {
            adminSignInRepository.save(adminDto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to register admin: "+e.getMessage());
        }

    }


}