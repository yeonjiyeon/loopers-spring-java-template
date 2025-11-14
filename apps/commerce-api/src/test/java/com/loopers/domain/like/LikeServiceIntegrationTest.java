package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.utils.DatabaseCleanUp;
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
class LikeServiceIntegrationTest {

  @Autowired
  LikeService likeService;

  @MockitoSpyBean
  LikeRepository likeRepository;

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

  @DisplayName("상품 좋아요 할 때,")
  @Nested
  class Create {

    @DisplayName("정상적인 요청이면 좋아요가 저장된다.")
    @Test
    void like_success_whenValidRequest() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product product = productRepository.save(new Product(1L, "상품A", "설명", 10000, 100));

      // act
      Like like = likeService.Like(user.getId(), product.getId());

      // assert
      assertAll(
          () -> assertThat(like).isNotNull(),
          () -> assertThat(like.getUserId()).isEqualTo(user.getId()),
          () -> assertThat(like.getProductId()).isEqualTo(product.getId())
      );
    }

    @DisplayName("이미 좋아요한 상품에 다시 요청하면, 중복 저장되지 않고 기존 좋아요 정보를 반환한다.- 멱등성")
    @Test
    void createLike_returnsExisting_whenAlreadyLiked() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product product = productRepository.save(new Product(1L, "상품A", "설명", 10000, 100));

      // act
      Like firstLike = likeService.Like(user.getId(), product.getId());
      Like secondLike = likeService.Like(user.getId(), product.getId());

      // assert
      assertAll(
          () -> assertThat(secondLike.getId()).isEqualTo(firstLike.getId())
      );
    }
  }

  @DisplayName("상품 좋아요 취소 할 때,")
  @Nested
  class UnlikeProduct {

    @DisplayName("좋아요 했던 상품을 취소하면 데이터가 삭제된다.")
    @Test
    void deleteLike_success_whenPreviouslyLiked() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product product = productRepository.save(new Product(1L, "상품A", "설명", 10000, 100));
      likeRepository.save(new Like(user.getId(), product.getId()));

      // act
      likeService.unLike(user.getId(), product.getId());

      // assert
      assertThat(likeRepository.findByUserIdAndProductId(user.getId(), product.getId())).isEmpty();
    }

    @DisplayName("좋아요 하지 않은 상품을 취소하면 예외 없이 정상 종료된다.")
    @Test
    void unlike_doNothing_whenNotLiked() {
      // arrange
      User user = userRepository.save(new User("userId", "a@email.com", "2025-11-11", Gender.MALE));
      Product product = productRepository.save(new Product(1L, "상품A", "설명", 10000, 100));

      // act & assert
      assertDoesNotThrow(() -> likeService.unLike(user.getId(), product.getId()));
    }
  }
}
