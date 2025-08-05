package com.example.bankcards.repository.sql;

import com.example.bankcards.entity.sql.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Boolean existsByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);
}
