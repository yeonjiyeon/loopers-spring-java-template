package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.loopers.domain.money.Money;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderCommand.Item;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.point.Point;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderFacadeIntegrationTest {

  @Autowired
  OrderFacade orderFacade;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @MockitoBean
  private PaymentService paymentService;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  private OrderCommand.PlaceOrder createOrderCommand(Long userId, List<Item> items) {
    return new OrderCommand.PlaceOrder(
        userId,
        items,
        "SAMSUNG",
        "1234-5678-1234-5678"
    );
  }

  @DisplayName("상품 주문시")
  @Nested
  class PlaceOrder {

    @DisplayName("주문이 성공하면 재고는 차감되지만, 카드 결제이므로 포인트는 차감되지 않는다.")
    @Test
    void placeOrder_success() {
      // arrange
      Long initialPoint = 100000L;
      User user = new User("userA", "a@email.com", "2025-11-11", Gender.MALE, new Point(initialPoint));
      User savedUser = userRepository.save(user);

      Long brandId = 1L;
      Product product = new Product(brandId, "Product A", "설명", new Money(20000L), 10);
      Product saveProduct = productRepository.save(product);

      Item itemCommand = new Item(saveProduct.getId(), 3);
      OrderCommand.PlaceOrder command = createOrderCommand(savedUser.getId(), List.of(itemCommand));

      Payment mockPayment = mock(Payment.class);

      given(mockPayment.getStatus()).willReturn(PaymentStatus.READY);
      given(mockPayment.getPgTxnId()).willReturn("T123456789");
      given(mockPayment.getAmount()).willReturn(new Money(60000L));
      given(paymentService.processPayment(any(), any(), any(), any()))
          .willReturn(mockPayment);

      // act
      OrderInfo result = orderFacade.placeOrder(command);

      // assert
      assertAll(
          () -> assertThat(result).isNotNull(),
          () -> assertThat(result.totalAmount()).isEqualTo(60000L),
          () -> assertThat(result.items()).hasSize(1)
      );

      Product updatedProduct = productRepository.findById(saveProduct.getId()).orElseThrow();
      assertThat(updatedProduct.getStock()).isEqualTo(7);
    }

    @DisplayName("재고가 부족하면 주문에 실패한다.")
    @Test
    void placeOrder_fail_insufficient_stock() {
      // arrange
      User user = userRepository.save(new User("userA", "a@email.com", "2025-11-11", Gender.MALE, new Point(100000L)));

      Product product = productRepository.save(
          new Product(1L, "Product A", "설명", new Money(20000L), 2)
      );

      Item itemCommand = new Item(product.getId(), 3); // 3개 주문 시도
      OrderCommand.PlaceOrder command = createOrderCommand(user.getId(), List.of(itemCommand));

      // act & assert
      CoreException exception = assertThrows(CoreException.class, () -> {
        orderFacade.placeOrder(command);
      });

      assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }
  }
}
