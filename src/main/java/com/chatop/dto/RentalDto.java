package com.chatop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentalDto {
    private Long id;
    private String name;
    private float surface;
    private float price;
    private String picture;
    private String description;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
