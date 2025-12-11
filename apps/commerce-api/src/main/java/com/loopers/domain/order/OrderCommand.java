package com.loopers.domain.order;

import java.util.List;

public class OrderCommand {

  public record PlaceOrder(
      Long userId,
      Long couponId,
      List<Item> items
  ) {

  }

  public record Item(
      Long productId,
      int quantity
  ) {

  }
}
