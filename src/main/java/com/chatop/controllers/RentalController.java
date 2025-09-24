package com.chatop.controllers;

import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.security.services.JWTService;
import com.chatop.services.RentalService;
import com.chatop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/rentals")
    public ResponseEntity<Map<String, String>> createRental(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("name") String name,
            @RequestParam("surface") float surface,
            @RequestParam("price") float price,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("description") String description

    ) {
        User owner = rentalService.handleUserFromToken(authorizationHeader);
        Rental rental = rentalService.handleCreateRental(name, surface, price, picture, description, owner);
        try {
            rentalService.createRental(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental could not be created. exception: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("ok", "Rental created !"));
    }
}
