package edu.icet.repository;

import edu.icet.model.dto.AdminDto;
import edu.icet.model.entity.AdminEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AdminSignInRepository {

    boolean save(AdminDto admin) throws SQLException, ClassNotFoundException;

}