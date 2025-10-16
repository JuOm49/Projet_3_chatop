package com.chatop.services;

import com.chatop.dto.ReturnRentalDto;
import com.chatop.dto.SurfacePriceDto;
import com.chatop.dto.RentalDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserService userService;

    @Value("${app.images.base-url}")
    private String imagesBaseUrl;

    @Value("${app.images.dir}")
    private String imagesDirectory;

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

    public void createRental(RentalDto rentalDto) throws IOException {
        if(rentalDto.getPicture().isEmpty() || rentalDto.getOwner_id() == null || rentalDto.getName().isEmpty()){
            throw new IllegalArgumentException("Missing required rental information.");
        }

        Rental rental;
        if(rentalDto.getId() != null){
            rental = rentalRepository.findById(rentalDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rental not found with ID: " + rentalDto.getId()));
            rental.setUpdatedAt(LocalDateTime.now());
        } else {
            rental = new Rental();
        }

        // Save the image file in the images directory
        saveImage(rentalDto.getPicture());

        rental.setName(rentalDto.getName());
        rental.setSurface(rentalDto.getSurface());
        rental.setPrice(rentalDto.getPrice());
        rental.setPicture(buildImageUrl(rentalDto.getPicture().getOriginalFilename()));
        rental.setDescription(rentalDto.getDescription());
        rental.setOwner(getOwner(rentalDto));

        rentalRepository.save(rental);
    }

    public void updateRental(RentalDto rentalDto) throws IOException {
        if( rentalDto.getOwner_id() == null || rentalDto.getName().isEmpty()){
            throw new IllegalArgumentException("Missing required rental information.");
        }

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
        rental.setDescription(rentalDto.getDescription());
        rental.setOwner(getOwner(rentalDto));

        rentalRepository.save(rental);
    }

    public SurfacePriceDto convertStringToFloatForSurfaceAndPrice(String surface, String price) {
        // Replace comma with dot for decimal conversion
        float surfaceFloat = Float.parseFloat(surface.replace(',', '.'));
        float priceFloat = Float.parseFloat(price.replace(',', '.'));
        if (surfaceFloat <= 0 || priceFloat <= 0 || Float.isNaN(surfaceFloat) || Float.isNaN(priceFloat)) {
            throw new IllegalArgumentException("Surface and price must be positive numbers.");
        }

        return new SurfacePriceDto(surfaceFloat, priceFloat);
    }

    public ReturnRentalDto convertToReturnRentalDto(Rental rental) {
        ReturnRentalDto returnRentalDto = new ReturnRentalDto();
        returnRentalDto.setId(rental.getId());
        returnRentalDto.setName(rental.getName());
        returnRentalDto.setSurface(rental.getSurface());
        returnRentalDto.setPrice(rental.getPrice());
        returnRentalDto.setPicture(rental.getPicture());
        returnRentalDto.setDescription(rental.getDescription());
        returnRentalDto.setOwner_id(rental.getOwner().getId());
        returnRentalDto.setCreated_at(rental.getCreatedAt());
        returnRentalDto.setUpdated_at(rental.getUpdatedAt());
        return returnRentalDto;
    }

    public RentalDto handleRentalDto(@Nullable Long id, String name, float surface, float price, @Nullable  MultipartFile picture, String description, User owner) {
        RentalDto rentalDto = new RentalDto();
        if(id != null){
            rentalDto.setId(id);
        }
        rentalDto.setName(name);
        rentalDto.setSurface(surface);
        rentalDto.setPrice(price);
        if(picture != null){
            rentalDto.setPicture(picture);
        }
        rentalDto.setDescription(description);
        rentalDto.setOwner_id(owner.getId());
        return rentalDto;
    }

    public String buildImageUrl(String filename) {
        return imagesBaseUrl + filename;
    }

    public void saveImage(MultipartFile picture) throws IOException {
        if ( picture == null || picture.isEmpty() || Objects.requireNonNull(picture.getOriginalFilename()).isEmpty()) return;
        Path imagesDir = Paths.get(imagesDirectory);
        if (!Files.exists(imagesDir)) {
            Files.createDirectories(imagesDir);
        }
        Path filePath = imagesDir.resolve(picture.getOriginalFilename());
        picture.transferTo(filePath.toFile());
    }

    private User getOwner(RentalDto rentalDto) {
        return this.userService.getUserById(rentalDto.getOwner_id())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with ID: " + rentalDto.getOwner_id()));
    }
}
