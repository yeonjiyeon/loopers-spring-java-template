package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

  List<Order> findByUserId(Long userId);
}
