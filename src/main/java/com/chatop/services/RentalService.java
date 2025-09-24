package com.chatop.services;

import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;
import com.chatop.security.services.JWTService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Data
@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    private JwtDecoder jwtDecoder;
    private JWTService jwtService;
    private UserService userService;

    public RentalService(JwtDecoder jwtDecoder, JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
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


    public Rental handleCreateRental(String name, float surface, float price, org.springframework.web.multipart.MultipartFile picture, String description, User owner) {
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setPicture(picture.getOriginalFilename());
        rental.setDescription(description);
        rental.setOwner(owner);

        return rental;
    }

    public void createRental(Rental rental) {
        if(rental.getPicture().isEmpty() || rental.getOwner() == null || rental.getName().isEmpty()){
            throw new IllegalArgumentException("Missing required rental information.");
        }

        rentalRepository.save(rental);
    }
}
