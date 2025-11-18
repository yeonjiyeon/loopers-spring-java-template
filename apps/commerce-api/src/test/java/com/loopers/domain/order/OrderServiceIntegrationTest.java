package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
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
      Product productA = productRepository.save(new Product(1L, "상품명1", "설명1", new Money(30000L), 5));
      Product productB = productRepository.save(new Product(1L, "상품명2", "설명2", new Money(50000L), 5));

      List<OrderItem> orderItems = List.of(
          new OrderItem(productA, 2),
          new OrderItem(productB, 1)
      );

      // act
      Order createdOrder = orderService.createOrder(user.getId(), orderItems);

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

  @DisplayName("주문 목록 조회")
  @Nested
  class GetList {

    @DisplayName("존재하는 유저 ID로 조회하면 주문 목록이 반환된다.")
    @Test
    void return_orderList_whenUserHasOrders() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product productA = productRepository.save(new Product(1L, "상품명1", "설명1", new Money(30000L), 5));
      Product productB = productRepository.save(new Product(1L, "상품명2", "설명2", new Money(50000L), 5));

      List<OrderItem> orderItems = List.of(
          new OrderItem(productA, 2),
          new OrderItem(productB, 1)
      );

      Product productC = productRepository.save(new Product(1L, "상품명3", "설명3", new Money(20000L), 10));
      Product productD = productRepository.save(new Product(1L, "상품명4", "설명4", new Money(10000L), 8));

      List<OrderItem> orderItems2 = List.of(
          new OrderItem(productC, 1),
          new OrderItem(productD, 3)
      );

      orderRepository.save(new Order(user.getId(), orderItems));
      orderRepository.save(new Order(user.getId(), orderItems2));


      // act
      List<Order> results = orderService.getOrders(user.getId());

      // assert
      assertAll(
          () -> assertThat(results).hasSize(2),
          () -> assertThat(results.get(0).getUserId()).isEqualTo(user.getId()),
          () -> assertThat(results.get(0).getOrderItems()).isNotEmpty(),
          () -> assertThat(results.get(0).calculateTotalAmount()).isGreaterThan(0)
      );
    }

    @DisplayName("해당 유저의 주문이 없으면 빈 리스트가 반환된다.")
    @Test
    void return_emptyList_whenUserHasNoOrders() {
      // arrange
      User user = userRepository.save(new User("user", "noorder@email.com", "2025-11-11", Gender.FEMALE));

      // act
      List<Order> result = orderService.getOrders(user.getId());

      // assert
      assertAll(
          () -> assertThat(result).isEmpty()
      );
    }
  }

  @DisplayName("단일 주문 상세 조회")
  @Nested
  class Get {

    @DisplayName("존재하는 주문 ID로 조회하면 해당 아이디의 이 반환된다.")
    @Test
    void return_orderInfo_whenUserHasOrders() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product productA = productRepository.save(new Product(1L, "상품명1", "설명1", new Money(30000L), 5));
      Product productB = productRepository.save(new Product(1L, "상품명2", "설명2", new Money(50000L), 5));

      List<OrderItem> orderItems = List.of(
          new OrderItem(productA, 2),
          new OrderItem(productB, 1)
      );

      Order savedOrder = orderRepository.save(new Order(user.getId(), orderItems));

      // act
      Order result = orderService.getOrder(savedOrder.getId());

      // assert
      assertAll(
          () -> assertThat(result.getId()).isEqualTo(savedOrder.getId()),
          () -> assertThat(result.getUserId()).isEqualTo(user.getId()),

          () -> assertThat(result.getOrderItems()).hasSize(2),

          () -> assertThat(result.calculateTotalAmount()).isEqualTo(110000)

      );
    }

    @DisplayName("존재하지 않는 주문 ID로 조회하면 예외가 발생한다.")
    @Test
    void throw_exception_whenOrderNotFound() {
      // arrange
      Long nonExistentOrderId = 99999L;

      ///act
      CoreException exception = assertThrows(CoreException.class, () -> {
        orderService.getOrder(nonExistentOrderId);
      });

      // assert
      assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }
  }
}
