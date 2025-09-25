package com.chatop.security.services;

import com.chatop.models.User;
import com.chatop.services.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.List;

@Data
@Service
public class AuthenticationService {

    private JwtDecoder jwtDecoder;
    private JWTService jwtService;
    private UserService userService;

    public AuthenticationService(JwtDecoder jwtDecoder, JWTService jwtService, UserService userService) {
        this.jwtDecoder = jwtDecoder;
        this.jwtService = jwtService;
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

    public User handleUserFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Jwt jwt = this.jwtDecoder.decode(token);
        String userMail = this.jwtService.getUserMailFromToken(jwt);

        if (userMail == null) {
            throw new UsernameNotFoundException("User email not found in token");
        }

        return this.userService.findByEmail(userMail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
