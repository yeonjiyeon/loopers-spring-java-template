package com.loopers.application.payment;

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
  ) {}

  public record PaymentRequestFailedEvent(
      Long orderId,
      Long couponId,
      String errorMessage
  ) {}
}
