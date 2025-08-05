package com.example.bankcards.repository;

import com.example.bankcards.entity.Balance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceRepository extends CrudRepository<Balance, UUID> {
}
