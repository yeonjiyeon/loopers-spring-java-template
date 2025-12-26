package com.loopers.application.like.event;

import com.loopers.domain.event.DomainEvent;
import java.util.UUID;

public record LikeCreatedEvent(
    String eventId,
    long productId,
    int increment,
    long timestamp
) implements DomainEvent {

  public static LikeCreatedEvent of(Long productId, int increment) {
    return new LikeCreatedEvent(
        UUID.randomUUID().toString(),
        productId,
        increment,
        System.currentTimeMillis()
    );
  }
}
