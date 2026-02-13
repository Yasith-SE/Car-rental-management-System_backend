package edu.icet.repository;

import edu.icet.model.dto.LoginUsers;

import java.sql.SQLException;

public interface LoginRepository {

    String validateLogin(LoginUsers loginUsers) throws SQLException, ClassNotFoundException;
}
