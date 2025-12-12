package com.loopers.domain.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String eventType;

  @Lob
  private String eventPayload;

  private String failureReason;
  private int retryCount;
  private LocalDateTime createdAt;

  public FailedEvent(String eventType, String eventPayload, String failureReason, int retryCount, LocalDateTime createdAt) {
    this.eventType = eventType;
    this.eventPayload = eventPayload;
    this.failureReason = failureReason;
    this.retryCount = retryCount;
    this.createdAt = createdAt;
  }

  public void incrementRetryCount() {
    this.retryCount++;
  }
}
