package org.himcharm.services;

import org.himcharm.dtos.UserRegisterDTO;
import org.himcharm.dtos.UserResponseDTO;
import org.himcharm.entities.User;

import java.util.List;

public interface UserService {
    UserRegisterDTO addUser(UserRegisterDTO registerDTO);
    User findUserByUsername(String username);
    User findById(String id);
    List<UserResponseDTO> getAllUsers();
}
