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

        //saveImage(rentalDto.getPicture());

        rental.setName(rentalDto.getName());
        rental.setSurface(rentalDto.getSurface());
        rental.setPrice(rentalDto.getPrice());
        rental.setDescription(rentalDto.getDescription());
        rental.setOwner(getOwner(rentalDto));

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

    public ReturnRentalDto conversionRentalToRentalDto(Rental rental) {
        ReturnRentalDto rentalDto = new ReturnRentalDto();
        rentalDto.setId(rental.getId());
        rentalDto.setName(rental.getName());
        rentalDto.setSurface(rental.getSurface());
        rentalDto.setPrice(rental.getPrice());
        rentalDto.setPicture(rental.getPicture());
        rentalDto.setDescription(rental.getDescription());
        rentalDto.setOwner_id(rental.getOwner().getId());
        rentalDto.setCreated_at(rental.getCreatedAt());
        rentalDto.setUpdated_at(rental.getUpdatedAt());
        return rentalDto;
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
