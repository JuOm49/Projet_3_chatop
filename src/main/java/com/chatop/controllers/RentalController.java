package com.chatop.controllers;

import com.chatop.dto.RentalDto;
import com.chatop.dto.ReturnRentalDto;
import com.chatop.dto.SurfacePriceDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.security.services.AuthenticationService;
import com.chatop.services.RentalService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;
    private final AuthenticationService  authenticationService;

    @Value("${app.images.dir}")
    private String imagesDirectory;

    public RentalController(RentalService rentalService, AuthenticationService authenticationService) {
        this.rentalService = rentalService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/rentals")
    public ResponseEntity<Map<String, Iterable<ReturnRentalDto>>> getAllRentals() {
        Iterable<Rental> rentals = rentalService.getAllRentals();

        if(rentals == null){
            return ResponseEntity.ok(Map.of("rentals", List.of()));
        }
        List<ReturnRentalDto> rentalsDto = new ArrayList<ReturnRentalDto>();

        rentals.forEach(rental->
                rentalsDto.add(rentalService.conversionRentalToRentalDto(rental)));

        return ResponseEntity.ok(Map.of("rentals",rentalsDto));
    }

    @GetMapping("/rentals/{id}")
    public ResponseEntity<ReturnRentalDto> getRental(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        ReturnRentalDto rentalDto = rentalService.conversionRentalToRentalDto(rental);

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

        User owner = authenticationService.handleUserFromToken(authorizationHeader);
        RentalDto rentalDto = rentalService.handleRentalDto(null, name, surfacePriceDto.getSurface(), surfacePriceDto.getPrice(), picture, description, owner);
        try {
            rentalService.createRental(rentalDto);
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
            @RequestParam("description") String description
    ) {
        SurfacePriceDto surfacePriceDto  = rentalService.conversionStringToFloatForSurfaceAndPrice(surface, price);

        User owner = authenticationService.handleUserFromToken(authorizationHeader);
        Rental rentalExist = rentalService.getRentalById(id);
        if (rentalExist == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental not found."));
        }
        if (!rentalExist.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("Error", "You are not authorized to update this rental."));
        }

        RentalDto rentalDto = rentalService.handleRentalDto(id, name, surfacePriceDto.getSurface(), surfacePriceDto.getPrice(), null, description, owner);
        try {
            rentalService.updateRental(rentalDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Rental could not be updated. exception: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Rental updated !"));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(imagesDirectory).resolve(filename);
            Resource file = new UrlResource(imagePath.toUri());
            if (file.exists() || file.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                        .body(file);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (java.net.MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
