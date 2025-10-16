package com.chatop.security.controllers;

import com.chatop.dto.UserAuthDto;
import com.chatop.dto.UserDto;
import com.chatop.models.User;
import com.chatop.security.services.AuthenticationService;
import com.chatop.security.services.JWTService;
import com.chatop.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JWTService jwtService;

    public AuthenticationController(UserService userService, AuthenticationService authenticationService, JWTService jwtService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@RequestHeader("Authorization") String authorizationHeader) {
        User owner = authenticationService.handleUserFromToken(authorizationHeader);

        if (owner == null) {
            return ResponseEntity.status(401).build();
        }

        UserDto userDto = userService.convertToUserDto(owner);
        if (userDto == null) {
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> Register(@RequestBody UserAuthDto userAuthDto){
        User newUser = userService.saveUser(userAuthDto);
        Authentication authentication = authenticationService.handleUsernamePasswordAuthenticationToken(newUser);

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> Login(@RequestBody UserAuthDto userAuthDto){
        User userLogin = userService.findByEmail(userAuthDto.getEmail()).orElse(null);

        User user = userService.userAuthDtoToUser(userAuthDto);

        Authentication authentication = authenticationService.handleUsernamePasswordAuthenticationToken(userLogin, user);

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }
}