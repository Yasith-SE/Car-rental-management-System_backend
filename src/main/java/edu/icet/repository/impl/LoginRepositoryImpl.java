package edu.icet.repository.impl;

import edu.icet.DBconnection.DBConnection;
import edu.icet.repository.LoginRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepositoryImpl implements LoginRepository {
    public String validateLogin(String email, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();

        String sql = "SELECT role FROM user_details WHERE email = ? AND password = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, email);
        pstm.setString(2, password);

        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            return rs.getString("role");
        }
        return null;
    }

}
