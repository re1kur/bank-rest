package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {

    @Query("""
                SELECT t FROM Transaction t
                WHERE (:amount IS NULL OR t.amount = :amount)
                  AND (:receiverCardId IS NULL OR t.receiverCard.id = :receiverCardId)
                  AND (:senderCardId IS NULL OR t.senderCard.id = :senderCardId)
                  AND (
                        (:cardIds IS NULL OR t.senderCard.id IN :cardIds OR t.receiverCard.id IN :cardIds)
                      )
            """)
    Page<Transaction> findAll(
            Pageable pageable,
            @Param("amount") BigDecimal amount,
            @Param("receiverCardId") UUID receiverCardId,
            @Param("senderCardId") UUID senderCardId,
            @Param("cardIds") List<UUID> cardIds
    );

}
