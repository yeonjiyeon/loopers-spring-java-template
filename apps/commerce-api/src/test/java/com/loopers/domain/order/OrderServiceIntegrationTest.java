package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class OrderServiceIntegrationTest {

  @Autowired
  OrderService orderService;

  @MockitoSpyBean
  OrderRepository orderRepository;

  @MockitoSpyBean
  UserRepository userRepository;

  @MockitoSpyBean
  ProductRepository productRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("주문 할 때,")
  @Nested
  class Create {

    @DisplayName("존재하는 상품 ID들로 주문 요청 시 주문이 정상적으로 생성되고 저장된다.")
    @Test
    void createOrder_whenValidProductIds_thenOrderIsCreatedAndSaved() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product productA = productRepository.save(new Product(1L, "상품명1", "설명1", 30000, 5));
      Product productB = productRepository.save(new Product(1L, "상품명2", "설명2", 50000, 5));

      List<OrderItem> itemRequests = List.of(
          new OrderItem(productA, 2),
          new OrderItem(productB, 1)
      );

      // act
      Order createdOrder = orderService.createOrder(user.getId(), itemRequests);

      // assert
      assertAll(
          () -> assertThat(createdOrder.getId()).isNotNull(),
          () -> assertThat(createdOrder.getOrderItems()).hasSize(2),
          () -> assertThat(createdOrder.getOrderItems())
              .extracting("productId")
              .containsExactlyInAnyOrder(productA.getId(), productB.getId()),
          () -> assertThat(createdOrder.getOrderItems())
              .extracting("quantity")
              .containsExactlyInAnyOrder(2, 1)
      );
    }
  }
}
