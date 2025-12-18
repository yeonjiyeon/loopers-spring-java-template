package com.loopers.application.like;

import com.loopers.application.like.event.LikeCreatedEvent;
import com.loopers.domain.event.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventOutboxHandler {
  private final KafkaTemplate<Object, Object> kafkaTemplate;
  private final OutboxService outboxService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(LikeCreatedEvent event) {
    outboxService.saveEvent("PRODUCT_METRICS", String.valueOf(event.productId()), event);

    kafkaTemplate.send("catalog-events", String.valueOf(event.productId()), event)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            outboxService.markPublished(event.eventId());
          } else {
            log.error("카프카 전송 실패: {}", ex.getMessage());
          }
        });
  }
}
