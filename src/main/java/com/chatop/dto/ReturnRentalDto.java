package com.chatop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnRentalDto {
    private Long id;
    private String name;
    private float surface;
    private float price;
    private String picture;
    private String description;
    private Long owner_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
