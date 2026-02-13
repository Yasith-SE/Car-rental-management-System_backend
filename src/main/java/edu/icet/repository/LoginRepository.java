package edu.icet.repository;

import java.sql.SQLException;

public interface LoginRepository {

    String validateLogin(String email, String password) throws SQLException, ClassNotFoundException;
}
