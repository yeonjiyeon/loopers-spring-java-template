package com.loopers.application.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BrandFacadeIntegrationTest {

  @Autowired
  BrandFacade brandFacade;

  @Autowired
  BrandRepository brandRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("브랜드 상세 정보 조회 시")
  @Nested
  class GetBrandInfo {

    @DisplayName("브랜드와 상품이 존재하면, BrandInfo DTO가 정상 반환된다.")
    @Test
    void return_brandInfo_whenBrandAndProductsExist() {
      // arrange
      Brand brand = brandRepository.save(new Brand("Nike", "Just Do It."));

      productRepository.save(new Product(brand.getId(), "Air Max", "설명1", new Money(150000L), 10));
      productRepository.save(new Product(brand.getId(), "Air Force", "설명2", new Money(130000L), 10));
      productRepository.save(new Product(brand.getId(), "Jordan", "설명3", new Money(200000L), 10));

      Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

      // act
      BrandInfo result = brandFacade.getBrandInfo(brand.getId(), pageable);

      // assert
      assertAll(
          () -> assertThat(result.brandId()).isEqualTo(brand.getId()),
          () -> assertThat(result.brandName()).isEqualTo("Nike"),

          () -> assertThat(result.products().getTotalElements()).isEqualTo(3),
          () -> assertThat(result.products().getTotalPages()).isEqualTo(2),
          () -> assertThat(result.products().getNumber()).isEqualTo(0)
      );
    }

    @DisplayName("존재하지 않는 브랜드 ID로 조회하면 예외가 발생한다.")
    @Test
    void throw_exception_whenBrandNotFound() {
      // arrange
      Long nonExistentBrandId = 99999L;
      Pageable pageable = PageRequest.of(0, 10);

      // act & assert
      CoreException exception = assertThrows(CoreException.class, () -> {
        brandFacade.getBrandInfo(nonExistentBrandId, pageable);
      });

      assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }
  }
}

