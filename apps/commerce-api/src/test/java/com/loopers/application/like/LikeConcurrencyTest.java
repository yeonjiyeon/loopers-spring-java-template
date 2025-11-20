package com.loopers.application.like;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.user.UserJpaRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LikeConcurrencyTest {
  @Autowired
  private LikeFacade likeFacade;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private com.loopers.utils.DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @Nested
  @DisplayName("좋아요 증가 동시성 (비관적 락)")
  class LikeIncreaseConcurrency {

    private Product targetProduct;
    private List<User> users;

    @BeforeEach
    void setUp() {
      Product product = new Product(1l, "인기상품", "설명", new Money(10000L), 100);
      this.targetProduct = productRepository.save(product);

      this.users = IntStream.range(0, 10)
          .mapToObj(i -> userJpaRepository.save(
              new User("user" + i, "user" + i + "@email.com", "2000-01-01", User.Gender.MALE)
          ))
          .toList();
    }

    @Test
    @DisplayName("동시에 10명이 좋아요를 누르면, 상품의 좋아요 개수는 정확히 10개가 되어야 한다.")
    void like_concurrency_test() throws InterruptedException {
      // arrange
      int threadCount = 10;
      ExecutorService executorService = Executors.newFixedThreadPool(32);
      CountDownLatch latch = new CountDownLatch(threadCount);

      AtomicInteger successCount = new AtomicInteger();
      AtomicInteger failCount = new AtomicInteger();

      // act
      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        executorService.submit(() -> {
          try {
            likeFacade.like(users.get(index).getId(), targetProduct.getId());
            successCount.getAndIncrement();
          } catch (Exception e) {
            System.out.println("좋아요 실패: " + e.getMessage());
            failCount.getAndIncrement();
          } finally {
            latch.countDown();
          }
        });
      }

      latch.await();

      Product resultProduct = productRepository.findById(targetProduct.getId()).orElseThrow();

      System.out.println("좋아요 성공: " + successCount.get() + ", 실패: " + failCount.get());

      // assert
      assertThat(successCount.get()).isEqualTo(threadCount);
      assertThat(resultProduct.getLikeCount()).isEqualTo(10);
    }
  }

  @Nested
  @DisplayName("좋아요 취소 동시성 (비관적 락)")
  class LikeDecreaseConcurrency {

    private Product targetProduct;
    private List<User> users;

    @BeforeEach
    void setUp() {
      Product product = new Product(1l, "인기상품", "설명", new Money(10000L), 100);
      this.targetProduct = productRepository.save(product);

      this.users = IntStream.range(0, 10)
          .mapToObj(i -> userJpaRepository.save(
              new User("user" + i, "user" + i + "@email.com", "2000-01-01", User.Gender.MALE)
          ))
          .toList();

      for (User user : users) {
        likeFacade.like(user.getId(), targetProduct.getId());
      }
    }

    @Test
    @DisplayName("이미 좋아요를 누른 10명이 동시에 취소를 요청하면, 좋아요 개수는 0개가 되어야 한다.")
    void unlike_concurrency_test() throws InterruptedException {
      // arrange
      int threadCount = 10;

      Product initialProduct = productRepository.findById(targetProduct.getId()).orElseThrow();
      assertThat(initialProduct.getLikeCount()).isEqualTo(10);

      ExecutorService executorService = Executors.newFixedThreadPool(32);
      CountDownLatch latch = new CountDownLatch(threadCount);

      AtomicInteger successCount = new AtomicInteger();
      AtomicInteger failCount = new AtomicInteger();

      // act
      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        executorService.submit(() -> {
          try {
            likeFacade.unLike(users.get(index).getId(), targetProduct.getId());
            successCount.getAndIncrement();
          } catch (Exception e) {
            System.out.println("취소 실패: " + e.getMessage());
            failCount.getAndIncrement();
          } finally {
            latch.countDown();
          }
        });
      }

      latch.await();


      Product resultProduct = productRepository.findById(targetProduct.getId()).orElseThrow();

      System.out.println("취소 성공: " + successCount.get() + ", 실패: " + failCount.get());

      // assert
      assertThat(successCount.get()).isEqualTo(threadCount);
      assertThat(resultProduct.getLikeCount()).isZero();
    }
  }
}
