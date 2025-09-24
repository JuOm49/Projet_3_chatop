package com.chatop.security.controllers;

import com.chatop.models.User;
import com.chatop.security.services.JWTService;
import com.chatop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    private final JWTService jwtService;

    public AuthenticationController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> Register(@RequestBody User user){
      User newUser = userService.saveUser(user);
        if(newUser == null){
            return ResponseEntity.badRequest().body(Map.of("Error", "User could not be created."));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser.getEmail(),
                null,
                List.of()
        );

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> Login(@RequestBody User user){

        Optional<User> userLogin = userService.findByEmail(user.getEmail());

        if(userLogin.isEmpty() || !userService.getPasswordEncoder().matches(user.getPassword(), userLogin.get().getPassword())){
            return ResponseEntity.badRequest().body(Map.of("Error", "User not found or incorrect credentials."));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userLogin.get().getEmail(),
                null,
                List.of()
        );

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(authentication)));
    }

}
