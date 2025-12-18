package com.loopers.domain.event;

import com.fasterxml.jackson.core.JsonProcessingException;
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
      String payload = objectMapper.writeValueAsString(event);
      OutboxEvent outboxEvent = new OutboxEvent(
          aggregateType,
          aggregateId,
          event.getClass().getSimpleName(),
          payload
      );
      outboxRepository.save(outboxEvent);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("이벤트 직렬화 실패", e);
    }
  }
}
