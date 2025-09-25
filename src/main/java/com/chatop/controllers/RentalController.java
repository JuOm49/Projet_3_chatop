package com.chatop.controllers;

import com.chatop.dto.RentalDto;
import com.chatop.dto.SurfacePriceDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.services.RentalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping("/rentals/{id}")
    public ResponseEntity<RentalDto> getRental(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rental.getId());
        rentalDto.setName(rental.getName());
        rentalDto.setSurface(rental.getSurface());
        rentalDto.setPrice(rental.getPrice());
        rentalDto.setPicture(rental.getPicture());
        rentalDto.setDescription(rental.getDescription());
        rentalDto.setOwnerId(rental.getOwner().getId());
        rentalDto.setCreatedAt(rental.getCreatedAt());
        rentalDto.setUpdatedAt(rental.getUpdatedAt());

        return ResponseEntity.ok(rentalDto);
    }

    @PostMapping("/rentals")
    public ResponseEntity<Map<String, String>> createRental(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("description") String description

    ) {
        SurfacePriceDto surfacePriceDto  = rentalService.conversionStringToFloatForSurfaceAndPrice(surface, price);

        User owner = rentalService.handleUserFromToken(authorizationHeader);
        RentalDto rentalDto = rentalService.handleRentalDto(null, name, surfacePriceDto.getSurface(), surfacePriceDto.getPrice(), picture, description, owner);
        try {
            rentalService.createOrUpdateRental(rentalDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental could not be created. exception: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Rental created !"));
    }

    @PutMapping("/rentals/{id}")
    public ResponseEntity<Map<String, String>> updateRental(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("description") String description
    ) {
        SurfacePriceDto surfacePriceDto  = rentalService.conversionStringToFloatForSurfaceAndPrice(surface, price);

        User owner = rentalService.handleUserFromToken(authorizationHeader);
        Rental rentalExist = rentalService.getRentalById(id);
        if (rentalExist == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental not found."));
        }
        if (!rentalExist.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("Error", "You are not authorized to update this rental."));
        }

        RentalDto rentalDto = rentalService.handleRentalDto(id, name, surfacePriceDto.getSurface(), surfacePriceDto.getPrice(), picture, description, owner);
        try {
            rentalService.createOrUpdateRental(rentalDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental could not be updated. exception: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Rental updated !"));
    }
}
