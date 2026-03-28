package edu.icet.service.impl;

import edu.icet.model.dto.LoginDto;
import edu.icet.model.dto.UserDto;
import edu.icet.model.entity.User;
import edu.icet.repository.UserRepository;
import edu.icet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto registerUser(UserDto userDto) {
        // Check if email already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setPostalCode(userDto.getPostalCode());

        // Default all new signups to CUSTOMER
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        // Check if passwords match
        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        return mapToDto(user);
    }

    // Helper mapper
    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPostalCode(user.getPostalCode());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }
}