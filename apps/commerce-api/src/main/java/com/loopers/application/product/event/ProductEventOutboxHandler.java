package com.loopers.application.product.event;

import com.loopers.domain.event.OutboxService;
import com.loopers.event.ProductViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductEventOutboxHandler {

  private final OutboxService outboxService;
  private final KafkaTemplate<Object, Object> kafkaTemplate;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ProductViewEvent event) {
    outboxService.saveEvent("PRODUCT_VIEW", String.valueOf(event.productId()), event);

    kafkaTemplate.send("catalog-events", String.valueOf(event.productId()), event)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            outboxService.markPublished(event.eventId());
          }
        });
  }
}
