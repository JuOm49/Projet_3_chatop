package com.chatop.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String message;
    private Long user_id;
    private Long rental_id;
}
