package edu.icet.service;

import edu.icet.model.dto.AdminDto;
import edu.icet.model.dto.Users;
import edu.icet.model.entity.AdminEntity;
import edu.icet.repository.AdminSignInRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                 null,
                 admin.getName(),
                 admin.getDateOfBirth(),
                 admin.getEmail(),
                 admin.getPassword(),
                 admin.getAddress(),
                 admin.getPostalCode()
         ));

    }
    public boolean authentication(Users adminUserLogin){
        Optional<AdminEntity> adminEntity = adminSignInRepository.findByEmail(adminUserLogin.getEmail());

        return adminEntity.isPresent() && adminEntity.get().getPassword().equals(adminUserLogin.getPassword());


    }

}
