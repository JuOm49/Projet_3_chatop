package com.chatop.services;

import com.chatop.dto.SurfacePriceDto;
import com.chatop.dto.RentalDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserService userService;

    public RentalService(RentalRepository rentalRepository, UserService userService) {
        this.rentalRepository = rentalRepository;
        this.userService = userService;
    }

    public Iterable<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public void createOrUpdateRental(RentalDto rentalDto) {
        if(rentalDto.getPicture().isEmpty() || rentalDto.getOwnerId() == null || rentalDto.getName().isEmpty()){
            throw new IllegalArgumentException("Missing required rental information.");
        }
        User owner = this.userService.getUserById(rentalDto.getOwnerId())
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

    public RentalDto conversionRentalToRentalDto(Rental rental) {
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
        return rentalDto;
    }

    public RentalDto handleRentalDto(@Nullable Long id, String name, float surface, float price, MultipartFile picture, String description, User owner) {
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
}
