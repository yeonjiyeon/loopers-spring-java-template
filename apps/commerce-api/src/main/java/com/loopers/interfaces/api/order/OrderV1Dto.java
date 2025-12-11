package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderInfo;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.payment.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class OrderV1Dto {
  public record OrderRequest(
      @NotEmpty(message = "주문 상품은 필수입니다.")
      List<OrderItemRequest> items,
      Long couponId,
      @NotNull(message = "결제 방식은 필수입니다.")
      PaymentType paymentType,
      String cardType,
      String cardNo
  ) {

    public OrderCommand.PlaceOrder toCommand(Long userId) {
      List<OrderCommand.Item> commandItems = items.stream()
          .map(item -> new OrderCommand.Item(item.productId(), item.quantity()))
          .toList();

      return new OrderCommand.PlaceOrder(
          userId,
          couponId,
          commandItems,
          paymentType,
          cardType,
          cardNo
      );
    }
  }

  public record OrderItemRequest(
      @NotNull Long productId,
      @Positive int quantity
  ) {}

  public record OrderResponse(
      Long orderId,
      Long userId,
      long totalPrice
  ) {

    public static OrderResponse from(OrderInfo response) {
      return new OrderResponse(
          response.orderId(),
          response.userId(),
          response.totalAmount()
      );
    }
  }
}

