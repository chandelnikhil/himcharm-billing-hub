package org.himcharm.services;

import org.himcharm.entities.User;

import java.util.List;

public interface UserService {
    User addUser(User user);
    User findUserByUsername(String username);
    User findById(String id);
    List<User> getAllUsers();
}
