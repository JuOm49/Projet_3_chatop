package com.chatop.models;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//@Data is a Lombok annotation that generates getters, setters, toString, equals, and hashCode methods.
@Data
@Entity
@Table(name="messages")
public class Message {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="rental_id", nullable = true)
    private Rental rental;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id", nullable = true)
    private User user;

    private String message;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

}
