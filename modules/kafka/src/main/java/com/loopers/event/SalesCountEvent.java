package com.loopers.event;

import java.util.UUID;

public record SalesCountEvent(
    String eventId,
    Long productId,
    int quantity,
    long timestamp
) {

  public static SalesCountEvent of(Long productId, int quantity) {
    return new SalesCountEvent(
        UUID.randomUUID().toString(),
        productId,
        quantity,
        System.currentTimeMillis()
    );
  }
}
