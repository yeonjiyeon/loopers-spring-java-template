package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class BrandServiceIntegrationTest {

  @Autowired
  BrandService brandService;

  @MockitoSpyBean
  BrandRepository brandRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("브랜드 조회 할 때,")
  @Nested
  class Get {

    @DisplayName("해당 ID 의 브랜드가 존재할 경우, 브랜드 정보가 반환된다")
    @Test
    void return_brandInfo_whenValidIdIsProvided() {
      // arrange
      Brand brand = new Brand("테스트 브랜드", "테스트 설명");
      Brand saveBrand = brandRepository.save(brand);
      Long findId = saveBrand.getId();

      // act


      Brand result = brandService.getBrand(findId);

      // assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertThat(result.getName()).isEqualTo("테스트 브랜드"),
          () -> assertThat(result.getDescription()).isEqualTo("테스트 설명")
      );
    }

    @DisplayName("존재하지 않는 브랜드 ID로 조회 시 404 Not Found 에러와 “브랜드를 찾을 수 없습니다.” 메시지를 반환한다.")
    @Test
    void throwsException_whenBrandNotFound() {
      // arrange
      Long findId = 1L;

      //act
      CoreException exception = assertThrows(CoreException.class, () -> {
        brandService.getBrand(findId);
      });

      // assert
      assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }
  }
}
