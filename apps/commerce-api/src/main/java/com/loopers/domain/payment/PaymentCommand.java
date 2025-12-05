package com.loopers.domain.payment;

import com.loopers.interfaces.api.payment.PaymentV1Dto.PaymentRequest;

public class PaymentCommand {

  public record CreatePayment(
      Long userId,
      Long orderId,
      Long amount,
      CardType cardType,
      String cardNo
  ) {

    public static CreatePayment from(Long userId, PaymentRequest request) {
      return new CreatePayment(
          userId,
          request.orderId(),
          request.amount(),
          request.cardType(),
          request.cardNo()
      );
    }
  }
}
