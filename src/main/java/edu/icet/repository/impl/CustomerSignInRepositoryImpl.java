package edu.icet.repository.impl;

import edu.icet.DBconnection.DBConnection;
import edu.icet.model.dto.CustomerDto;
import edu.icet.repository.CustomerSignInRepository;

import java.sql.*;

public class CustomerSignInRepositoryImpl implements CustomerSignInRepository {

    public boolean save(CustomerDto customer) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            String custSql = "INSERT INTO customer (name, date_of_birth, email, password, address, postalCode) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement custPstm = connection.prepareStatement(custSql, Statement.RETURN_GENERATED_KEYS);
            custPstm.setString(1, customer.getName());
            custPstm.setObject(2, customer.getDateOfBirth());
            custPstm.setString(3, customer.getEmail());
            custPstm.setString(4, customer.getPassword());
            custPstm.setString(5, customer.getAddress());
            custPstm.setInt(6, customer.getPostalCode());
            custPstm.executeUpdate();

            ResultSet rs = custPstm.getGeneratedKeys();
            if (rs.next()) {
                int custId = rs.getInt(1);

                String userSql = "INSERT INTO user_details (email, password, role, customer_id) VALUES (?, ?, 'customer', ?)";
                PreparedStatement userPstm = connection.prepareStatement(userSql);
                userPstm.setString(1, customer.getEmail());
                userPstm.setString(2, customer.getPassword());
                userPstm.setInt(3, custId);
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
