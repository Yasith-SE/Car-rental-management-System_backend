package edu.icet.service;

import edu.icet.model.dto.AuthResponseDto;
import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDto registerUser(UserDto userDto, User creator);

    AuthResponseDto loginUser(LoginDto loginDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    void deleteUser(Long id);

    UserDto updateUserRole(Long id, String newRole, User actor);

    UserDto updateUserAccessStatus(Long id, String nextStatus, User actor);

    UserDto updateProfileImage(Long id, MultipartFile imageFile);

    UserDto toDto(User user);
}
