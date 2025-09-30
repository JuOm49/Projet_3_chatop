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

    /**
     * Handles authentication by creating an authentication token for the given user.
     * @param user
     * @return
     * @throws IllegalArgumentException if user is null
     * List.of() can be replaced with roles (ROLE_USER, ROLE_ADMIN) or authorities if needed
     * null credentials (password) in the token for security reasons
     */
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

    /**
     * Handles authentication by verifying user credentials and creating an authentication token.
     * @param userLogin
     * @param user
     * @return
     * @throws IllegalArgumentException if user is not found or credentials are incorrect
     * List.of() can be replaced with roles (ROLE_USER, ROLE_ADMIN) or authorities if needed
     * null credentials (password) in the token for security reasons
     */
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

    /**
     * Extracts the user from the JWT token present in the Authorization header.
     * @param authorizationHeader The Authorization header containing the Bearer token.
     * @return The User associated with the token.
     * @throws UsernameNotFoundException if the user email is not found in the token or if the user does not exist.
     */
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
