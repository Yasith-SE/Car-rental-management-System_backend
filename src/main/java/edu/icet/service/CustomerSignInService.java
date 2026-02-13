package edu.icet.service;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.dto.Users;

import java.util.List;

public interface CustomerSignInService {

    void registerCustomer(CustomerDto customerDto);

    boolean login(String email, String password);

    List<Users> getAllAvailableCars();

}