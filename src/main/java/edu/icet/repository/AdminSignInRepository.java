package edu.icet.repository;

import edu.icet.model.dto.AdminDto;
import java.sql.SQLException;

public interface AdminSignInRepository {

    boolean save(AdminDto admin) throws SQLException, ClassNotFoundException;

}