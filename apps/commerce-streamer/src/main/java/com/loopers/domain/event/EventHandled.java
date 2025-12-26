package com.loopers.domain.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "event_handled")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventHandled {

  @Id
  private String eventId;
  private LocalDateTime processedAt;

  public EventHandled(String eventId) {
    this.eventId = eventId;
    this.processedAt = LocalDateTime.now();
  }
}
