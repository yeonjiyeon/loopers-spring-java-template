package com.loopers.domain.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.infrastructure.event.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Transactional(propagation = Propagation.MANDATORY)
  public void saveEvent(String aggregateType, String aggregateId, Object event) {
    try {
      String eventId = (String) event.getClass().getMethod("eventId").invoke(event);
      String payload = objectMapper.writeValueAsString(event);
      OutboxEvent outboxEvent = new OutboxEvent(
          eventId,
          aggregateType,
          aggregateId,
          event.getClass().getSimpleName(),
          payload
      );
      outboxRepository.save(outboxEvent);
    } catch (Exception e) {
      throw new RuntimeException("Outbox 저장 실패", e);
    }
  }

  @Transactional
  public void markPublished(String eventId) {
    outboxRepository.findByEventId(eventId).ifPresent(event -> {
      event.markPublished();
      outboxRepository.save(event);
    });
  }

  public void markFailed(String eventId) {
    outboxRepository.findByEventId(eventId).ifPresent(event -> {
      event.markFailed();
      outboxRepository.save(event);
    });
  }
}
