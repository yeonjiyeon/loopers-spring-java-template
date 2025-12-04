package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderInfo;
import com.loopers.domain.order.OrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class OrderV1Dto {
  public record OrderRequest(
      @NotEmpty(message = "주문 상품은 필수입니다.")
      List<OrderItemRequest> items,

      @NotBlank(message = "카드 종류는 필수입니다.")
      String cardType,

      @NotBlank(message = "카드 번호는 필수입니다.")
      String cardNo
  ) {

    public OrderCommand.PlaceOrder toCommand(Long userId) {
      List<OrderCommand.Item> commandItems = items.stream()
          .map(item -> new OrderCommand.Item(item.productId(), item.quantity()))
          .toList();

      return new OrderCommand.PlaceOrder(
          userId,
          commandItems,
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
      long totalPrice,
      String transactionId,
      String paymentStatus
  ) {

    public static OrderResponse from(OrderInfo response) {
      return new OrderResponse(
          response.orderId(),
          response.userId(),
          response.totalAmount(),
          response.transactionId(), 
          response.paymentStatus()
      );
    }
  }
}

