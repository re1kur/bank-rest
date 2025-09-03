package com.example.bankcards.repository;

import com.example.bankcards.entity.OutboxEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OutboxRepository extends CrudRepository<OutboxEvent, UUID> {
    @Modifying
    @Query("""
        UPDATE OutboxEvent e
        SET e.status = 'PROCESSING'
        WHERE e.id = :id AND e.status <> 'PROCESSING'
        """)
    int markAsProcessing(@Param("id") UUID id);

    @Modifying
    @Query("""
        UPDATE OutboxEvent e
        SET e.status = 'ERROR'
        WHERE e.id = :id
        """)
    int markAsError(@Param("id") UUID id);
}
