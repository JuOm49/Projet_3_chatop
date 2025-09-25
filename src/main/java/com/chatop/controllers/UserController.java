package com.chatop.controllers;

import com.chatop.dto.UserDto;
import com.chatop.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id).map(
                user -> {
                    UserDto userDto = userService.conversionUserToUserDto(user);
                    return ResponseEntity.ok(userDto);
                }
        ).orElse(ResponseEntity.notFound().build());
    }
}
