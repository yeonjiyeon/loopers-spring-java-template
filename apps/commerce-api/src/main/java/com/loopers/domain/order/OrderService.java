package com.loopers.domain.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderService {

  private final OrderRepository orderRepository;

  public Order createOrder(Long userId, List<OrderItem> orderItems) {
    return orderRepository.save(new Order(userId, orderItems));
  }
}
