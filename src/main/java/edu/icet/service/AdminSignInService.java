package edu.icet.service;

import edu.icet.model.dto.AdminDto;
import edu.icet.model.entity.AdminEntity;
import edu.icet.repository.AdminSignInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminSignInService {

    @Autowired
    AdminSignInRepository adminSignInRepository;

    public List<AdminDto> getAllAdmin(){

        List<AdminEntity> adminEntity = adminSignInRepository.findAll();
        List<AdminDto> adminDtos = new ArrayList<>();

        for(AdminEntity entityAdmin : adminEntity){
            adminDtos.add(new AdminDto(

                    entityAdmin.getId(),
                    entityAdmin.getName(),
                    entityAdmin.getDateOfBirth(),
                    entityAdmin.getEmail(),
                    entityAdmin.getPassword(),
                    entityAdmin.getAddress(),
                    entityAdmin.getPostalCode()

            ));

        }
        return adminDtos;
    }

}
