package edu.icet.service;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.entity.CustomerEntity;
import edu.icet.repository.CustomerEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerSignInServices {



    @Autowired
    CustomerEntityRepository customerEntityRepository;


    public List<CustomerDto> employeeDtos(){
        List<CustomerEntity> customerEntities = customerEntityRepository.findAll();
        List<CustomerDto> customerDto = new ArrayList<>();

        for(CustomerEntity customerEntity : customerEntities){
            customerDto.add(new CustomerDto(
                    customerEntity.getId(),
                    customerEntity.getName(),
                    customerEntity.getDateOfBirth(),
                    customerEntity.getEmail(),
                    customerEntity.getPassword(),
                    customerEntity.getAddress(),
                    customerEntity.getPostalCode()
            ));
        }
        return  customerDto;
    }

    public void addCustomer(CustomerEntity customerEntity){
        customerEntityRepository.save(new CustomerEntity(
                customerEntity.getId(),
                customerEntity.getName(),
                customerEntity.getDateOfBirth(),
                customerEntity.getEmail(),
                customerEntity.getPassword(),
                customerEntity.getAddress(),
                customerEntity.getPostalCode()
        ));



    }



}
