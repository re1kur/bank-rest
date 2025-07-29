package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Boolean existsByUsername(String username);
}
