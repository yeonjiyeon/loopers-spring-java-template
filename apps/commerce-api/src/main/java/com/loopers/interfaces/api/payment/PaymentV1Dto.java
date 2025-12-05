package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentInfo;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentStatus;

public class PaymentV1Dto {

  public record PaymentRequest(
      Long orderId,
      CardType cardType,
      String cardNo,
      Long amount
  ) {

  }

  public record PaymentResponse(
      String paymentId,
      Long orderId,
      CardType cardType,
      Long amount,
      PaymentStatus status
  ) {

    public static PaymentResponse from(PaymentInfo paymentInfo) {
      return new PaymentResponse(
          paymentInfo.paymentId(),
          paymentInfo.orderId(),
          paymentInfo.cardType(),
          paymentInfo.amount(),
          paymentInfo.status()
      );
    }
  }

  public record CallbackRequest(
      String transactionKey,
      String orderId,
      String status,
      String reason
  ) {

  }

  public record CallbackResponse(
      String status,
      String message
  ) {

    public static CallbackResponse success() {
      return new CallbackResponse("SUCCESS", "Callback processed successfully");
    }

    public static CallbackResponse fail(String message) {
      return new CallbackResponse("FAIL", message);
    }
  }
}
