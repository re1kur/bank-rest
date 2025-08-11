package com.example.bankcards.entity;

import com.example.bankcards.core.dto.transaction.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transactions")
@Entity
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Transaction transaction)) return false;

        return id.equals(transaction.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
