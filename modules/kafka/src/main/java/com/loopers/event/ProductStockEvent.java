package com.loopers.event;

import java.util.UUID;

public record ProductStockEvent(
    String eventId,
    Long productId,
    int sellQuantity,
    int currentStock,
    long price,
    long timestamp
) {
  public static ProductStockEvent of(Long productId, int sellQuantity, int currentStock, long price) {
    return new ProductStockEvent(
        UUID.randomUUID().toString(),
        productId,
        sellQuantity,
        currentStock,
        price,
        System.currentTimeMillis()
    );
  }
}
