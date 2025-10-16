package com.chatop.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class RentalDto {
    private Long id;
    private String name;
    private float surface;
    private float price;
    private MultipartFile picture;
    private String description;
    private Long owner_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
