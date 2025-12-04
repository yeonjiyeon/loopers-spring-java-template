package com.loopers.application.order;

import com.loopers.domain.money.Money;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.payment.Payment;
import java.util.List;

public record OrderInfo(
    Long orderId,
    Long userId,
    List<OrderItemInfo> items,
    long totalAmount,
    String transactionId,
    String paymentStatus
) {

  public static OrderInfo from(Order order, Payment payment) {
    return new OrderInfo(
        order.getId(),
        order.getUserId(),
        order.getOrderItems().stream().map(OrderItemInfo::from).toList(),
        order.getTotalAmount().getValue(),
        payment.getTransactionId(),
        payment.getStatus().name()
    );
  }

  public record OrderItemInfo(Long productId, int quantity, Money price) {

    public static OrderItemInfo from(OrderItem item) {
      return new OrderItemInfo(
          item.getProductId(),
          item.getQuantity(),
          item.getPrice()
      );
    }
  }
}
