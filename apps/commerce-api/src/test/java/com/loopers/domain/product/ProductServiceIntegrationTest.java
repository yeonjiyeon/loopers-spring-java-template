package com.loopers.domain.product;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class ProductServiceIntegrationTest {

  @Autowired
  ProductService productService;

  @MockitoSpyBean
  ProductRepository productRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("상품 조회 할 때,")
  @Nested
  class Get {

    @DisplayName("기본 정렬(최신순)로 첫 페이지 조회 시, 페이징 정보와 상세 필드(이름/설명/가격/재고)가 올바르게 반환된다.")
    @Test
    void return_productList_whenValidIdIsProvided() {

      // arrange
      for (int i = 1; i <= 25; i++) {
        Product product = new Product(
            1l,
            "상품명" + i,
            "설명" + i,
            1000 * i,
            10 * i
        );
        productRepository.save(product);
      }

      // act
      Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<Product> resultsPage = productService.getProducts(pageable);

      // assert
      assertAll(
          () -> assertEquals(20, resultsPage.getSize(), "현재 페이지 사이즈 검증(20개)"),
          () -> assertEquals(2, resultsPage.getTotalPages(), "총 페이지 수(25개 / 20개씩 총 2개)."),
          () -> assertEquals(25, resultsPage.getTotalElements(), "총 상품 개수(25개)"),
          () -> assertTrue(resultsPage.hasNext(), "다음 페이지 여부(true)"),

          () -> assertEquals("상품명25", resultsPage.getContent().get(0).getName(), "가장 최신 상품(상품명25)"),
          () -> assertEquals("상품명6", resultsPage.getContent().get(19).getName(), "20번째 상품(상품명6)"),

          () -> {
            Product firstProduct = resultsPage.getContent().get(0);
            assertAll("첫 번째 상품 상세 필드 검증",
                () -> assertEquals("상품명25", firstProduct.getName()),
                () -> assertEquals("설명25", firstProduct.getDescription()),
                () -> assertEquals(25000, firstProduct.getPrice()),
                () -> assertEquals(250, firstProduct.getStock()),
                () -> assertEquals(1L, firstProduct.getBrandId())
            );
          }

      );
    }
  }
}
