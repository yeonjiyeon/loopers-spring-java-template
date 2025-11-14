package com.loopers.interfaces.order;

import java.util.List;

public class OrderV1Dto {

  public record OrderRequest(List<OrderItemRequest> items) {

  }

  public record OrderItemRequest(Long productId,
                                 int quantity
  ) {

  }
}
