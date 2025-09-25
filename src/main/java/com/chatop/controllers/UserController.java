package com.chatop.controllers;

import com.chatop.dto.UserDto;
import com.chatop.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userService.getUser(id).map(
                user -> {
                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setEmail(user.getEmail());
                    userDto.setName(user.getName());
                    userDto.setCreatedAt(user.getCreatedAt());
                    userDto.setUpdatedAt(user.getUpdatedAt());
                    return ResponseEntity.ok(userDto);
                }
        ).orElse(ResponseEntity.notFound().build());
    }
}
