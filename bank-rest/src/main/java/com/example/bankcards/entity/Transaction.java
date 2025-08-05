package com.example.bankcards.entity;

import com.example.bankcards.core.dto.transaction.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transactions")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_card_id")
    private Card senderCard;

    @ManyToOne
    @JoinColumn(name = "receiver_card_id")
    private Card receiverCard;

    private BigDecimal amount;

    @Column(insertable = false, columnDefinition = "DEFAULT 'processing'")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(insertable = false, columnDefinition = "DEFAULT now()")
    private LocalDateTime issueTimestamp;

    @Column(insertable = false)
    private LocalDateTime processedTimestamp;
}
