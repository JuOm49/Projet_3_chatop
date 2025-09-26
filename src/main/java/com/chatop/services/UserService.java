package com.chatop.services;

import com.chatop.dto.UserAuthDto;
import com.chatop.dto.UserDto;
import com.chatop.models.User;
import com.chatop.repositories.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.stream.StreamSupport;
import java.util.Optional;

@Data
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserById(final Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(final UserAuthDto newRegisterUserDto) {

        User newUser = userAuthDtoToUser(newRegisterUserDto);

        Optional<User> userFind = findByEmail(newUser.getEmail());
        if (userFind.isPresent()) {
            throw new IllegalArgumentException("User already exists.");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return StreamSupport.stream(getUsers().spliterator(), false)
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setCreated_at(user.getCreatedAt());
        userDto.setUpdated_at(user.getUpdatedAt());

        return userDto;
    }

    public User userAuthDtoToUser(UserAuthDto registerUserDto) {
        User user = new User();
        user.setEmail(registerUserDto.getEmail());
        user.setName(registerUserDto.getName());
        user.setPassword(registerUserDto.getPassword());
        return user;
    }

    private Iterable<User> getUsers() {
        return userRepository.findAll();
    }
}
