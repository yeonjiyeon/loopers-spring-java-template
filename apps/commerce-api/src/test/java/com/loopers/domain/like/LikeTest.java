package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LikeTest {

  @DisplayName("Like 객체 생성 테스트")
  @Nested
  class Create {

    @DisplayName("모든 값이 유효하면 Like 객체를 생성에 성공한다.")
    @Test
    void create_like_with_valid_data() {
      // assert
      assertDoesNotThrow(() -> {
        new Like(1L, 100L);
      });
    }

    @DisplayName("Like 객체 생성 실패 테스트")
    @Nested
    class LikeValidation {

      @Test
      @DisplayName("유저 ID(userId)가 없으면(null), Like 객체 생성에 실패한다.")
      void throwsBadRequest_whenUserIdIsNull() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Like(null, 100L);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("상품 ID(productId)가 없으면(null), Like 객체 생성에 실패한다.")
      void throwsBadRequest_whenProductIdIsNull() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Like(1L, null);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
    }
  }
}
