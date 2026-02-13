package edu.icet.repository;

import edu.icet.model.dto.CustomerDto;
import edu.icet.model.entity.CustomerEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface CustomerSignInRepository {
    boolean save(CustomerDto customer) throws SQLException, ClassNotFoundException;


}
