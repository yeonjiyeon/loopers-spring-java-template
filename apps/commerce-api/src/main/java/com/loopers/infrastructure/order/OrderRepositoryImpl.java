package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;


  @Override
  public Order save(Order order) {
    return orderJpaRepository.save(order);
  }

  @Override
  public List<Order> findByUserId(Long userId) {
    return orderJpaRepository.findByUserId(userId);
  }

  @Override
  public Optional<Order> findById(Long id) {
    return orderJpaRepository.findById(id);
  }


}
