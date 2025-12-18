package com.loopers.domain.order;

import com.loopers.domain.payment.PaymentType;
import java.util.List;
import java.util.Map;

public class OrderCommand {

  public record PlaceOrder(
      Long userId,
      Long couponId,
      List<Item> items,
      PaymentType paymentType,
      String cardType,
      String cardNo
  ) {
    public Map<String, Object> getPaymentDetails() {
      return Map.of(
          "cardType", cardType,
          "cardNo", cardNo
      );
  }


  }

  public record Item(
      Long productId,
      int quantity
  ) {

  }
}
