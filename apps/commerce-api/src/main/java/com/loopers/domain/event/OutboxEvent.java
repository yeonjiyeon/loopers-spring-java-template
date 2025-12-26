package com.loopers.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "outbox_event")
@NoArgsConstructor
public class OutboxEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String eventId;
  private String aggregateType;
  private String aggregateId;
  private String eventType;      // 예: LikeCreatedEvent

  @Column(columnDefinition = "TEXT")
  private String payload;

  @Enumerated(EnumType.STRING)
  private OutboxStatus status = OutboxStatus.INIT;

  private int retryCount = 0;
  private LocalDateTime createdAt = LocalDateTime.now();

  public OutboxEvent(String eventId, String aggregateType, String aggregateId, String eventType, String payload) {
    this.eventId = eventId;
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
  }

  public void markPublished() {
    this.status = OutboxStatus.PUBLISHED;
  }

  public void markFailed() {
    this.status = OutboxStatus.FAILED;
    this.retryCount++;
  }
}
