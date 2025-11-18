package com.loopers.interfaces.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class OrderV1Dto {

  public record OrderRequest(List<OrderItemRequest> items) {

  }

  public record OrderItemRequest(@NotNull Long productId,
                                 @Positive int quantity
  ) {

  }
}
