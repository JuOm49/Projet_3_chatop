package com.chatop.services;

import com.chatop.dto.MessageDto;
import com.chatop.models.Message;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.MessageRepository;

import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final RentalService rentalService;

    public MessageService(MessageRepository messageRepository, UserService userService, RentalService rentalService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    public void saveMessage(Message message) {
        if(message.getMessage().isEmpty() || message.getUser() == null || message.getRental() == null) {
            throw new IllegalArgumentException("Message, UserId and RentalId are required.");
        }

        messageRepository.save(message);
    }

    public Message prepareMessageForSave(MessageDto messageDto) {

        Rental rental = rentalService.getRentalById(messageDto.getRentalId());
        User user = userService.getUserById(messageDto.getUserId()).orElse(null);

        if(rental == null || user == null) {
            throw new IllegalArgumentException("Invalid RentalId or UserId.");
        }

        Message message = new Message();
        message.setMessage(messageDto.getMessage());
        message.setRental(rental);
        message.setUser(user);

        return message;
    }
}
