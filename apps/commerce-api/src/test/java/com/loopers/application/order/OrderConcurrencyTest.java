package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.money.Money;
import com.loopers.domain.order.OrderCommand.Item;
import com.loopers.domain.order.OrderCommand.PlaceOrder;
import com.loopers.domain.point.Point;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderConcurrencyTest {

  @Autowired
  private OrderFacade orderFacade;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserJpaRepository userJpaRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private ProductJpaRepository productJpaRepository;

  private User savedUser;
  private Product savedProduct;

  @BeforeEach
  void setUp() {
    User user = new User("testUserId", "test@email.com", "1990-01-01", User.Gender.MALE,
        new Point(10000L));
    this.savedUser = userJpaRepository.saveAndFlush(user);

    Product product = new Product(1L, "테스트상품", "상품 설명", new Money(1000L), 100);
    this.savedProduct = productJpaRepository.saveAndFlush(product);
  }

  @AfterEach
  void tearDown() {
    userJpaRepository.deleteAll();
    productJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("동시에 10명이 주문을 해도 포인트는 순차적으로 정확히 차감되어야 한다.")
  void concurrency_point_deduction_test() throws InterruptedException {
    // given
    int threadCount = 10;

    PlaceOrder command = new PlaceOrder(savedUser.getId(), List.of(new Item(savedProduct.getId(), 1)));

    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger failCount = new AtomicInteger();


    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          orderFacade.placeOrder(command);
          successCount.getAndIncrement();
        } catch (Exception e) {
          System.out.println("주문 실패: " + e.getMessage());
          failCount.getAndIncrement();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    // then
    User findUser = userRepository.findById(savedUser.getId()).orElseThrow();

    long expectedPoint = 10000L - (1000L * threadCount);

    assertThat(successCount.get()).isEqualTo(threadCount);
    assertThat(findUser.getPoint().getAmount()).isEqualTo(expectedPoint);
  }
}
