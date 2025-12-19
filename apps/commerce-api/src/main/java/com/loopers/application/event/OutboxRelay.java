package com.loopers.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.event.OutboxEvent;
import com.loopers.domain.event.OutboxStatus;
import com.loopers.infrastructure.event.OutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

  private final OutboxRepository outboxRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final ObjectMapper objectMapper;

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void resendPendingEvents() {
    List<OutboxEvent> pendingEvents = outboxRepository.findTop10ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
        List.of(OutboxStatus.INIT, OutboxStatus.FAILED), 5
    );

    if (pendingEvents.isEmpty()) return;

    for (OutboxEvent outbox : pendingEvents) {
      try {
        Class<?> eventClass = Class.forName(outbox.getEventType());
        Object originalEvent = objectMapper.readValue(outbox.getPayload(), eventClass);

        eventPublisher.publishEvent(originalEvent);

        log.info("[Outbox Relay] 이벤트 재발행 성공: {}", outbox.getEventId());
        outbox.markPublished();

      } catch (Exception e) {
        log.error("[Outbox Relay] 재발행 실패: {}", outbox.getEventId());
        outbox.markFailed();
      }
    }
  }
}
