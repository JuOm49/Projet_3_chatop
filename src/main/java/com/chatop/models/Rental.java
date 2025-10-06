package com.chatop.models;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// @Data is a Lombok annotation that generates getters, setters, toString, equals, and hashCode methods.
@Data
@Entity
@Table(name="rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;

    private float surface;

    private float price;
    
    private String picture;

    private String description;

    // To benefit from the ManyToOne relationship,
    // the user class must be a JPA entity
    @ManyToOne(optional = false)
    @JoinColumn(name="owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Message message;
}
