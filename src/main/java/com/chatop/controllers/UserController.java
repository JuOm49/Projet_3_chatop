package com.chatop.controllers;

import com.chatop.models.User;
import com.chatop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id){
        return userService.getUser(id).orElse(null);
    }

//    @PostMapping("/auth/register")
//    public User registerUser(User newUser){
//        return userService.saveUser(newUser);
//    }

}
