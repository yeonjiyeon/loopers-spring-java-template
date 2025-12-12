package com.loopers.application.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.event.DomainEvent;
import com.loopers.domain.event.FailedEvent;
import com.loopers.infrastructure.event.FailedEventRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FailedEventStore implements FailedEventScheduler {

  private final FailedEventRepository failedEventRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  @Override
  public <T extends DomainEvent> void scheduleRetry(T event, String reason) {
    String payload;

    try {
      payload = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      return;
    }

    FailedEvent failedEvent = new FailedEvent(
        event.getClass().getName(),
        payload,
        reason,
        0,
        LocalDateTime.now()
    );

    try {
      failedEventRepository.save(failedEvent);
    } catch (Exception e) {
    }
  }
}
