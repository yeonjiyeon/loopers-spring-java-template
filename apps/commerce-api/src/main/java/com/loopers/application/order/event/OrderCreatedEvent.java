package com.loopers.application.order.event;

import com.loopers.domain.order.OrderItem;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.user.User;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
    String eventId,
    Long orderId,
    Long userId,
    List<OrderItemInfo> items,
    long finalAmount,
    PaymentType paymentType,
    String cardType,
    String cardNo,
    Long couponId
) {

  public record OrderItemInfo(Long productId, int quantity) {

  }

  public static OrderCreatedEvent of(
      Long orderId,
      User user,
      List<OrderItem> orderItems,
      long finalAmount,
      PaymentType paymentType,
      String cardType,
      String cardNo,
      Long couponId
  ) {

    List<OrderItemInfo> itemInfos = orderItems.stream()
        .map(item -> new OrderItemInfo(item.getProductId(), item.getQuantity()))
        .toList();

    return new OrderCreatedEvent(
        UUID.randomUUID().toString(),
        orderId,
        user.getId(),
        itemInfos,
        finalAmount,
        paymentType,
        cardType,
        cardNo,
        couponId
    );
  }
}
