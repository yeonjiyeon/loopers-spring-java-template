package com.loopers.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductViewEvent(
    String eventId,
    Long productId,
    LocalDateTime createdAt
) {

  public static ProductViewEvent from(Long productId) {
    return new ProductViewEvent(
        UUID.randomUUID().toString(),
        productId,
        LocalDateTime.now()
    );
  }
}
