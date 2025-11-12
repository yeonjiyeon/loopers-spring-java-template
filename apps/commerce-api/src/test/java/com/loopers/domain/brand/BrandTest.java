package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BrandTest {

  @DisplayName("Brand 객체 생성 테스트")
  @Nested
  class Create {

    @DisplayName("모든 값이 유효하면 Brand 객체를 생성에 성공한다.")
    @Test
    void create_brand_with_valid_data() {
      // assert
      assertDoesNotThrow(() -> {
        new Brand("name", "description");
      });
    }

    @DisplayName("Brand 객체 생성 실패 테스트")
    @Nested
    class BrandValidation {

      @Test
      @DisplayName("브랜드 이름(name)이 없거나(null) 공백이면, Brand 객체 생성에 실패한다.")
      void throwsBadRequest_whenBrandNameIsNullOrBlank() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Brand("", "description");
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("브랜드 설명(description)이 없거나(null) 공백이면, Brand 객체 생성은 실패한다.")
      void throwsBadRequest_whenBrandDescriptionIsNullOrBlank() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Brand("name", "");
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
    }
  }
}
