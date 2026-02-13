package edu.icet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icet.model.entity.AdminEntity;
import edu.icet.model.dto.AdminDto;
import edu.icet.repository.AdminSignInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSignInServiceImpl implements AdminSignInService {


    @Override
    public void addAdmin(AdminDto adminDto) {


    }

    @Override
    public boolean login(String email, String password) {


    }
}