package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.money.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductTest {

  @DisplayName("Product 객체 생성 테스트")
  @Nested
  class Create {

    @DisplayName("모든 값이 유효하면 Product 객체를 생성에 성공한다.")
    @Test
    void create_product_with_valid_data() {
      // assert
      assertDoesNotThrow(() -> {
        new Product(1L, "name", "description", new Money(1000L), 100);
      });
    }

    @DisplayName("Product 객체 생성 실패 테스트")
    @Nested
    class ProductValidation {

      @Test
      @DisplayName("브랜드id가 없으면 Product 객체 생성에 실패한다.")
      void shouldThrowException_whenBrandIdIsNull() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Product(null, "name", "description", new Money(1000L), 100);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
      @Test
      @DisplayName("제품 이름(description)이 없거나(null) 공백이면, Product 객체 생성은 실패한다.")
      void shouldThrowException_whenNameIsBlank() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Product(1L, "", "description", new Money(1000L), 100);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("제품 설명(description)이 없거나(null) 공백이면, Product 객체 생성은 실패한다.")
      void shouldThrowException_whenDescriptionIsBlank() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Product(1L, "name", "", new Money(1000L), 100);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("상품 가격이 0 미만이면 Product 객체 생성에 실패한다.")
      void shouldThrowException_whenPriceIsNegative() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Product(1L, "name", "description", new Money(-1L), 100);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("상품 재고가 0 미만이면 Product 객체 생성에 실패한다.")
      void shouldThrowException_whenStockIsNegative() {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new Product(1L, "name", "", new Money(1000L), -1);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
    }
  }
}
