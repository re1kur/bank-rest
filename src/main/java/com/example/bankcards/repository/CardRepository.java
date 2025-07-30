package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends CrudRepository<Card, UUID> {
    Boolean existsByNumber(String number);
}
