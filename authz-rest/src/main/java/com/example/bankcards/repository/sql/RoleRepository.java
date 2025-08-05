package com.example.bankcards.repository.sql;

import com.example.bankcards.entity.sql.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String roleName);

    Boolean existsByName(String name);

    Page<Role> findAll(Pageable pageable);
}
