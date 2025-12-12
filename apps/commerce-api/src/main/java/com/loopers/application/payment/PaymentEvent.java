package com.loopers.application.payment;

import com.loopers.domain.event.DomainEvent;

public class PaymentEvent {
  public record PaymentRequestedEvent(
      Long orderId,
      Long paymentId,
      Long couponId
  ) {}

  public record PaymentCompletedEvent(
      Long orderId,
      Long paymentId,
      boolean isSuccess,
      Long couponId
  ) implements DomainEvent {}

  public record PaymentRequestFailedEvent(
      Long orderId,
      Long couponId,
      String errorMessage
  ) {}
}
