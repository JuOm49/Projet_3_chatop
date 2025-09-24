package com.chatop.services;

import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.StreamSupport;

import java.util.Optional;

@Data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<User> getUser(final Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(final User newUser) {
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

    private Iterable<User> getUsers() {
        return userRepository.findAll();
    }

}
