package com.chatop.security.services;

import com.chatop.models.User;
import com.chatop.services.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.List;

@Data
@Service
public class AuthenticationService {

    private UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public Authentication handleUsernamePasswordAuthenticationToken(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User not found or incorrect credentials.");
        }
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of()
        );
    }

    public Authentication handleUsernamePasswordAuthenticationToken(User userLogin, User user) {
        if(userLogin == null || !userService.getPasswordEncoder().matches(user.getPassword(), userLogin.getPassword())){
            throw new IllegalArgumentException("User not found or incorrect credentials.");
        }
        return new UsernamePasswordAuthenticationToken(
                userLogin.getEmail(),
                null,
                List.of()
        );
    }
}
