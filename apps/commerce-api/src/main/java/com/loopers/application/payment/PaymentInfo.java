package com.loopers.application.payment;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;

public record PaymentInfo(
    String paymentId,
    Long orderId,
    CardType cardType,
    String cardNo,
    Long amount,
    PaymentStatus status,
    String transactionId) {

  public static PaymentInfo from(Payment payment) {
    return new PaymentInfo(
        payment.getId().toString(),
        payment.getOrderId(),
        payment.getCardType(),
        payment.getCardNo(),
        payment.getAmount().getValue(),
        payment.getStatus(),
        payment.getTransactionId()
    );
  }
}
