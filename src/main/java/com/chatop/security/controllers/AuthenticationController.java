package com.chatop.security.controllers;

import com.chatop.models.User;
import com.chatop.security.services.AuthenticationService;
import com.chatop.security.services.JWTService;
import com.chatop.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    private final JWTService jwtService;

    public AuthenticationController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> Register(@RequestBody User user){
        User newUser = userService.saveUser(user);
        Authentication authentication = authenticationService.handleUsernamePasswordAuthenticationToken(newUser);

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> Login(@RequestBody User user){
        User userLogin = userService.findByEmail(user.getEmail()).orElse(null);
        Authentication authentication = authenticationService.handleUsernamePasswordAuthenticationToken(userLogin, user);

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }
}