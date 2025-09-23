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
        //vérification du user avec requête faite avec getUsers, si ok
        //return save(user) sinon retourner un message d'erreur.
        boolean hasUserWithEmail =  StreamSupport.stream(getUsers().spliterator(), false)
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()));

        if (hasUserWithEmail) {
            throw new IllegalArgumentException("User with id " + newUser.getId() + " already exists.");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userRepository.save(newUser);
    }

    private Iterable<User> getUsers() {
        return userRepository.findAll();
    }

}
