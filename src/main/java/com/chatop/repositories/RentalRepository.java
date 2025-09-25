package com.chatop.repositories;

import com.chatop.models.Rental;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {
}
