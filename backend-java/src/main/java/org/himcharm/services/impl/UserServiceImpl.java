package org.himcharm.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.himcharm.entities.User;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.UserRepo;
import org.himcharm.services.UserService;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    @Override
    @Transactional
    public User addUser(User user) {
        return userRepo.save(user);
    }

    @Transactional
    @Override
    public User findUserByUsername(String username) {
        User user = userRepo.findByEmail(username);
        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    @Transactional
    @Override
    public User findById(String id) {
        return userRepo.findById(Long.parseLong(id)).get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .filter(user -> !user.getEmail().equals("skillhub.cloud.dep@gmail.com"))
                .toList();
    }
}
