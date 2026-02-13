package edu.icet.service.Impl;

import edu.icet.model.dto.LoginUsers;
import edu.icet.repository.LoginRepository;
import edu.icet.repository.impl.LoginRepositoryImpl;
import edu.icet.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class LoginServiceImpl implements LoginService {

    LoginRepository loginRepository = new LoginRepositoryImpl();

    @Override
    public String loginUser(LoginUsers loginUsers) {
        try {
            return loginRepository.validateLogin(loginUsers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Login error"+e.getMessage());
        }
    }
}
