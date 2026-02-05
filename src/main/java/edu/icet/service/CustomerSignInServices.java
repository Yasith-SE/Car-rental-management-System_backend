package edu.icet.service;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.dto.Users;
import edu.icet.model.entity.CustomerEntity;
import edu.icet.model.entity.UsersEntity;
import edu.icet.repository.CustomerEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerSignInServices {



    @Autowired
    private CustomerEntityRepository customerEntityRepository;


    public List<CustomerDto> getAllCustomers(){
        List<CustomerEntity> customerEntities = customerEntityRepository.findAll();
        List<CustomerDto> customerDto = new ArrayList<>();

        for(CustomerEntity customerEntity : customerEntities){
            customerDto.add(new CustomerDto(
                    customerEntity.getId(),
                    customerEntity.getName(),
                    customerEntity.getDateOfBirth(),
                    customerEntity.getEmail(),
                    null,
                    customerEntity.getAddress(),
                    customerEntity.getPostalCode()
            ));
        }
        return  customerDto;
    }
    public void addCustomer(CustomerDto customerDto){
        if(customerEntityRepository.existByEmail(customerDto.getEmail())){


        }

        customerEntityRepository.save(new CustomerEntity(
                customerDto.getId(),

                customerDto.getName(),
                customerDto.getDateOfBirth(),
                customerDto.getEmail(),
                customerDto.getPassword(),
                customerDto.getAddress(),
                customerDto.getPostalCode()
        ));

    }
    public boolean authenticate(Users loginRequest) {
        Optional<CustomerEntity> customer = customerEntityRepository.findbyEmail(loginRequest.getEmail());

        return customer.isPresent() && customer.get().getPassword().equals(loginRequest.getPassword());
    }



}
