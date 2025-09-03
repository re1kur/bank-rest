package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String payload;

    @Column(insertable = false)
    private LocalDateTime createdAt;

    private String type;

    @Column(insertable = false)
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof OutboxEvent event)) return false;

        if (id == null || event.id == null) return false;

        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : System.identityHashCode(this));
    }
}
