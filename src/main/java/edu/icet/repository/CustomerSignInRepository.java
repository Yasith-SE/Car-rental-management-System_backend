package edu.icet.repository;

import edu.icet.model.dto.CustomerDto;

import java.sql.SQLException;

public interface CustomerSignInRepository {
    boolean save(CustomerDto customer) throws SQLException, ClassNotFoundException;


}
