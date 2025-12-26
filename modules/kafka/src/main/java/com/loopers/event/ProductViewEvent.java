package com.loopers.event;

import java.util.UUID;

public record ProductViewEvent(
    String eventId,
    Long productId,
    long timestamp
) {

  public static ProductViewEvent from(Long productId) {
    return new ProductViewEvent(
        UUID.randomUUID().toString(),
        productId,
        System.currentTimeMillis()
    );
  }
}
