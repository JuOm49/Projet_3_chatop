package com.chatop.security.controllers;

import com.chatop.models.User;
import com.chatop.security.services.AuthenticationService;
import com.chatop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody User user){
        String token = "";
      User newUser = userService.saveUser(user);
        if(newUser != null){
            token = authenticationService.generateToken(newUser);

            if(token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Token has not been generated.");
            }
        }
        else {
            return ResponseEntity.badRequest().body("Error: User could not be created.");
        }

        return ResponseEntity.ok(Map.of("token", token));
    }

}
