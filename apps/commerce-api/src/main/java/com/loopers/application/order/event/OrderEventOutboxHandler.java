package com.loopers.application.order.event;

import com.loopers.domain.event.OutboxService;
import com.loopers.event.ProductStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventOutboxHandler {
  private final KafkaTemplate<Object, Object> kafkaTemplate;
  private final OutboxService outboxService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ProductStockEvent event) {
    kafkaTemplate.send("catalog-events", String.valueOf(event.productId()), event)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            outboxService.markPublished(event.eventId());
          } else {
            outboxService.markFailed(event.eventId());
          }
        });
  }
}
