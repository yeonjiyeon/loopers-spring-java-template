package com.loopers.domain.order;

public enum OrderStatus {
  PENDING_PAYMENT,
  PAYMENT_REQUESTED,
  PAYMENT_COMPLETED,
  PAYMENT_FAILED,
  CANCELED
}
