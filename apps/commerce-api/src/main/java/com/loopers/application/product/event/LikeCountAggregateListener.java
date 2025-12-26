package com.loopers.application.product.event;

import com.loopers.application.event.FailedEventStore;
import com.loopers.application.like.event.LikeCreatedEvent;
import com.loopers.event.LikeCountEvent;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeCountAggregateListener {

  private final ProductService productService;
  private final FailedEventStore failedEventStore;
  private final ApplicationEventPublisher eventPublisher;


  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLikeCreatedEvent(LikeCreatedEvent event) {

    try {
      int updatedLikeCount = performAggregation(event);

      eventPublisher.publishEvent(new LikeCountEvent(
          event.eventId(),
          event.productId(),
          updatedLikeCount
      ));

    } catch (ObjectOptimisticLockingFailureException e) {

      failedEventStore.scheduleRetry(event, "Optimistic Lock Conflict");

    } catch (Exception e) {

      failedEventStore.scheduleRetry(event, "Unexpected Error: " + e.getMessage());
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int performAggregation(LikeCreatedEvent event) {
    if (event.increment() > 0) {
      return productService.increaseLikeCount(event.productId());
    } else {
      return productService.decreaseLikeCount(event.productId());
    }
  }
}
