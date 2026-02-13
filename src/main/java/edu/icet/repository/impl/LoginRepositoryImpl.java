package edu.icet.repository.impl;

import edu.icet.DBconnection.DBConnection;
import edu.icet.model.dto.LoginUsers;
import edu.icet.repository.LoginRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepositoryImpl implements LoginRepository {
    public String validateLogin(LoginUsers loginUsers) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();

        String sql = "SELECT role FROM user_details WHERE email = ? AND password = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, loginUsers.getEmail());
        pstm.setString(2, loginUsers.getPassword());

        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            return rs.getString("role");
        }
        return null;
    }

}
