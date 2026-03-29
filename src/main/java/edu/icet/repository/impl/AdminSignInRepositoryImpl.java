package edu.icet.repository.impl;

import edu.icet.DBconnection.DBConnection;
import edu.icet.model.dto.AdminDto;
import edu.icet.repository.AdminSignInRepository;

import java.sql.*;


public class AdminSignInRepositoryImpl implements AdminSignInRepository {

    @Override
    public boolean save(AdminDto admin) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {

            String adminSql = "INSERT INTO admin (name, date_of_birth, email, password, address, postalCode) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement adminPstm = connection.prepareStatement(adminSql, Statement.RETURN_GENERATED_KEYS);
            adminPstm.setString(1, admin.getName());
            adminPstm.setObject(2, admin.getDateOfBirth());
            adminPstm.setString(3, admin.getEmail());
            adminPstm.setString(4, admin.getPassword());
            adminPstm.setString(5, admin.getAddress());
            adminPstm.setInt(6, admin.getPostalCode());
            adminPstm.executeUpdate();


            ResultSet rs = adminPstm.getGeneratedKeys();
            if (rs.next()) {
                int adminId = rs.getInt(1);

                String userSql = "INSERT INTO user_details (email, password, role, admin_id) VALUES (?, ?, 'admin', ?)";
                PreparedStatement userPstm = connection.prepareStatement(userSql);
                userPstm.setString(1, admin.getEmail());
                userPstm.setString(2, admin.getPassword());
                userPstm.setInt(3, adminId); // Setting FK
                userPstm.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
