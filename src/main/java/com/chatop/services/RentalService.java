package com.chatop.services;

import com.chatop.dto.SurfacePriceDto;
import com.chatop.dto.RentalDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;
import com.chatop.security.services.JWTService;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


    public RentalDto handleRentalDto(@Nullable Long id, String name, float surface, float price, org.springframework.web.multipart.MultipartFile picture, String description, User owner) {
        RentalDto rentalDto = new RentalDto();
        if(id != null){
            rentalDto.setId(id);
        }
        rentalDto.setName(name);
        rentalDto.setSurface(surface);
        rentalDto.setPrice(price);
        rentalDto.setPicture(picture.getOriginalFilename());
        rentalDto.setDescription(description);
        rentalDto.setOwnerId(owner.getId());
        return rentalDto;
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public void createOrUpdateRental(RentalDto rentalDto) {
        if(rentalDto.getPicture().isEmpty() || rentalDto.getOwnerId() == null || rentalDto.getName().isEmpty()){
            throw new IllegalArgumentException("Missing required rental information.");
        }
        User owner = this.userService.getUser(rentalDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with ID: " + rentalDto.getOwnerId()));

        Rental rental;
        if(rentalDto.getId() != null){
            rental = rentalRepository.findById(rentalDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rental not found with ID: " + rentalDto.getId()));
            rental.setUpdatedAt(LocalDateTime.now());
        } else {
            rental = new Rental();
        }

        rental.setName(rentalDto.getName());
        rental.setSurface(rentalDto.getSurface());
        rental.setPrice(rentalDto.getPrice());
        rental.setPicture(rentalDto.getPicture());
        rental.setDescription(rentalDto.getDescription());
        rental.setOwner(owner);

        rentalRepository.save(rental);
    }

    public SurfacePriceDto conversionStringToFloatForSurfaceAndPrice(String surface, String price) {
        float surfaceFloat = Float.parseFloat(surface.replace(',', '.'));
        float priceFloat = Float.parseFloat(price.replace(',', '.'));
        if (surfaceFloat <= 0 || priceFloat <= 0 || Float.isNaN(surfaceFloat) || Float.isNaN(priceFloat)) {
            throw new IllegalArgumentException("Surface and price must be positive numbers.");
        }

        return new SurfacePriceDto(surfaceFloat, priceFloat);
    }
}
