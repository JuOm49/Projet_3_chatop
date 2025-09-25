package com.chatop.controllers;

import com.chatop.dto.MessageDto;
import com.chatop.models.Message;
import com.chatop.services.MessageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, String>> createMessage(@RequestBody MessageDto messageDto) {

        if(messageDto.getRentalId() == null || messageDto.getMessage().isEmpty() || messageDto.getUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message, UserId and RentalId are required."));
        }

        Message message = messageService.prepareMessageForSave(messageDto);

        try {
            messageService.saveMessage(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("Error", "Message could not be created. exception: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Message send with success"));
    }
}
