package com.chatop.models;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//@Data is a Lombok annotation that generates getters, setters, toString, equals, and hashCode methods.
@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String email;

    private String name;

    private String password;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
