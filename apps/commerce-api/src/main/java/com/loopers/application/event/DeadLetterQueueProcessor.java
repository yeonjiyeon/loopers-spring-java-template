package com.loopers.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.event.DomainEvent;
import com.loopers.domain.event.FailedEvent;
import com.loopers.infrastructure.event.FailedEventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeadLetterQueueProcessor {

  private final FailedEventRepository failedEventRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final ObjectMapper objectMapper;

  private static final int MAX_RETRY_COUNT = 5;

  @Scheduled(fixedRate = 300000)
  @Transactional 
  public void retryFailedEvents() {

    List<FailedEvent> eventsToRetry = failedEventRepository.findByRetryCountLessThan(MAX_RETRY_COUNT);

    if (eventsToRetry.isEmpty()) return;

    for (FailedEvent failedEvent : eventsToRetry) {
      try {
        Class<?> eventClass = Class.forName(failedEvent.getEventType());
        DomainEvent originalEvent = (DomainEvent) objectMapper.readValue(
            failedEvent.getEventPayload(), eventClass);

        eventPublisher.publishEvent(originalEvent);
        failedEventRepository.delete(failedEvent);

      } catch (Exception e) {
        failedEvent.incrementRetryCount(); 
        failedEventRepository.save(failedEvent);
      }
    }
  }
}
