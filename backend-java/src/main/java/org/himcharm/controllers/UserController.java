package org.himcharm.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.UserResponseDTO;
import org.himcharm.entities.User;
import org.himcharm.services.UserService;
import org.modelmapper.ModelMapper;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
        ApiResponse response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users
        );
        return ResponseEntity.ok(response);
    }

    private UserResponseDTO toResponse(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
