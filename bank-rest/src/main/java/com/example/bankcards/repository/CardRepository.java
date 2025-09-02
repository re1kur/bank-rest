package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends CrudRepository<Card, UUID> {
    Boolean existsByNumberHash(String numberHash);

    @Query(
            value = """
        SELECT c FROM Card c
        JOIN c.balance b
        WHERE
            (:userId IS NULL OR c.user.id = :userId) AND
            (:status IS NULL OR c.status = :status) AND
            (:expirationDate IS NULL OR c.expirationDate = :expirationDate) AND
            (:amount IS NULL OR b.value = :amount)
        ORDER BY
            CASE WHEN :amountDesc = TRUE THEN b.value END DESC,
            CASE WHEN :amountDesc = FALSE THEN b.value END ASC,
            CASE WHEN :dateDesc = TRUE THEN c.expirationDate END DESC,
            CASE WHEN :dateDesc = FALSE THEN c.expirationDate END ASC
        """
    )
    Page<Card> findAll(
            Pageable pageable,
            @Param("amount") BigDecimal amount,
            @Param("amountDesc") Boolean amountDesc,
            @Param("expirationDate") LocalDateTime expirationDate,
            @Param("dateDesc") Boolean dateDesc,
            @Param("status") String status,
            @Param("userId") UUID userId
    );

    List<Card> findAllByUserId(UUID userId);
}
