package com.chatop.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String message;
    private Long userId;
    private Long rentalId;
}
