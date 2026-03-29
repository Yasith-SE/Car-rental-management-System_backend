package edu.icet.service;

import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto registerUser(UserDto userDto);

    UserDto loginUser(LoginDto loginDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    void deleteUser(Long id);

    UserDto updateUserRole(Long id, String newRole);
}