package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductFacadeIntegrationTest {
  @Autowired
  ProductFacade productFacade;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  BrandRepository brandRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("상품 목록 조회 시, 각 상품의 브랜드 이름을 포함하여 DTO 페이지로 반환한다.")
  @Test
  void return_productInfoPage_withBrandNames() {
    // arrange
    Brand brandA = brandRepository.save(new Brand("BrandA", "브랜드A"));
    Brand brandB = brandRepository.save(new Brand("BrandB", "브랜드B"));

    productRepository.save(new Product(brandA.getId(), "Product A", "설명", new Money(20000L), 10));
    productRepository.save(new Product(brandA.getId(), "Product B", "설명", new Money(15000L), 10));
    productRepository.save(new Product(brandB.getId(), "Product C", "설명", new Money(10000L), 10));
    productRepository.save(new Product(brandB.getId(), "Product D", "설명", new Money(30000L), 10));


    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

    // act
    Page<ProductInfo> result = productFacade.getProductsInfo(pageable);

    // assert
    assertAll(
        () -> assertThat(result.getTotalElements()).isEqualTo(4),
        () -> assertThat(result.getNumber()).isEqualTo(0)
    );
  }

  @DisplayName("상품이 없을 경우 빈 페이지를 반환한다.")
  @Test
  void return_emptyPage_whenNoProductsExist() {
    // arrange
    Pageable pageable = PageRequest.of(0, 10);

    // act
    Page<ProductInfo> result = productFacade.getProductsInfo(pageable);

    // assert
    assertThat(result.isEmpty()).isTrue();
  }
}
