package com.chatop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAuthDto {
    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
