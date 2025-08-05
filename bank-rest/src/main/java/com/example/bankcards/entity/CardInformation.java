package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "card_information")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardInformation {
    @Id
    private UUID cardId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;

    private String brand;

    @Column(insertable = false, columnDefinition = "DEFAULT CURRENT_DATE")
    private LocalDate issueDate;
}
