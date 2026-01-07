package edu.icet.service;

import edu.icet.model.dto.AdminDto;
import edu.icet.model.entity.AdminEntity;
import edu.icet.repository.AdminSignInRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminSignInService {

    @Autowired
    private AdminSignInRepository adminSignInRepository;

    @Transactional(readOnly = true)
    public List<AdminDto> getAllAdmin(){

        List<AdminEntity> adminEntity = adminSignInRepository.findAll();
        List<AdminDto> adminDtos = new ArrayList<>();

        for(AdminEntity entityAdmin : adminEntity){
            adminDtos.add(new AdminDto(

                    entityAdmin.getId(),
                    entityAdmin.getName(),
                    entityAdmin.getDateOfBirth(),
                    entityAdmin.getEmail(),
                    null,
                    entityAdmin.getAddress(),
                    entityAdmin.getPostalCode()

            ));

        }
        return adminDtos;
    }

    public void addAdmin(AdminDto admin){

        if(adminSignInRepository.existsByEmail(admin.getEmail())){
            throw new RuntimeException("Email already in there");

        }

         adminSignInRepository.save(new AdminEntity(
                 admin.getId(),
                 admin.getName(),
                 admin.getDateOfBirth(),
                 admin.getEmail(),
                 null,
                 admin.getAddress(),
                 admin.getPostalCode()
         ));

    }

}
