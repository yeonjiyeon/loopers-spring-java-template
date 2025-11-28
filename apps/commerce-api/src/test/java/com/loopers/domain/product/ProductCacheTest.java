package com.loopers.domain.product;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

import com.loopers.domain.money.Money;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.util.Set;
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
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class ProductCacheTest {
  @Autowired
  ProductService productService;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  private ProductJpaRepository productJpaRepository;

  @Autowired
  DatabaseCleanUp databaseCleanUp;

  @MockitoSpyBean
  RedisTemplate<String, Object> redisTemplate;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();

    Set<String> keys = redisTemplate.keys("product:*");
    if (!keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  @DisplayName("캐시 동작 검증")
  @Nested
  class Cache {

    @DisplayName("DB 조회 후 결과가 Redis에 저장되며, DB 데이터가 삭제되어도 캐시에서 조회된다.")
    @Test
    void return_cachedData_whenDbDataDeleted() {
      // arrange
      Long brandId = 1L;
      Product product = new Product(brandId, "캐시상품", "설명", new Money(10000L), 10);
      productRepository.save(product);

      Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
      String expectedKey = "product:list:brand:" + brandId + ":page:0:size:10:sort:id,DESC";

      // act 1: 첫 번째 조회 (Cache Miss -> DB 조회 -> Redis 저장)
      productService.getProductsByBrandId(brandId, pageable);

      // assert 1: Redis에 키가 생성되었는지 확인
      assertTrue(redisTemplate.hasKey(expectedKey), "Redis에 캐시 키가 생성되어야 함");

      // act 2: DB 데이터 강제 삭제 (변수 창출)
      productJpaRepository.deleteAll();

      // act 3: 두 번째 조회 (Cache Hit -> Redis 조회)
      Page<Product> secondResult = productService.getProductsByBrandId(brandId, pageable);

      // assert 2: DB는 비었지만 결과가 나와야 함
      assertAll("캐시 조회 검증",
          () -> assertEquals(1, secondResult.getTotalElements(), "DB 삭제 후에도 1개가 조회되어야 함"),
          () -> assertEquals("캐시상품", secondResult.getContent().get(0).getName(), "캐시된 상품명 일치 확인")
      );
    }

    @DisplayName("Redis 연결 장애가 발생해도 서비스는 DB를 통해 정상적으로 데이터를 반환한다 (Fail-Safe).")
    @Test
    void return_dataFromDb_whenRedisConnectionFails() {
      // arrange
      Long brandId = 2L;
      Product product = new Product(brandId, "장애대응상품", "설명", new Money(20000L), 20);
      productRepository.save(product);

      Pageable pageable = PageRequest.of(0, 10);


      ValueOperations<String, Object> ops = redisTemplate.opsForValue();

      doThrow(new RedisConnectionFailureException("Redis 연결 불가"))
          .when(redisTemplate).opsForValue();

      // act
      Page<Product> result = productService.getProductsByBrandId(brandId, pageable);

      // assert
      assertAll("장애 대응 검증",
          () -> assertEquals(1, result.getTotalElements(), "Redis 에러 시에도 데이터가 반환되어야 함"),
          () -> assertEquals("장애대응상품", result.getContent().get(0).getName())
      );
    }
  }

  @Nested
  @DisplayName("🔍 상품 상세 조회 캐시 검증")
  class CacheDetail {

    @Test
    @DisplayName("상세 조회 시 캐시가 저장되고, DB 삭제 후에도 조회된다")
    void return_cachedProduct_whenDbDataDeleted() {
      // arrange
      Long brandId = 1L;
      Product product = new Product(brandId, "상세보기 상품", "설명", new Money(5000L), 10);
      Product savedProduct = productRepository.save(product);
      Long productId = savedProduct.getId();

      String expectedKey = "product:detail:" + productId;

      // act 1: 첫 번째 조회 (Cache Miss -> DB 조회 -> Redis 저장)
      productService.getProduct(productId);

      // assert 1: Redis에 키가 생성되었는지 확인
      assertTrue(redisTemplate.hasKey(expectedKey), "상세 조회 후 Redis 키가 생성되어야 함");

      // act 2: DB 데이터 강제 삭제
      productJpaRepository.deleteAll();

      // act 3: 두 번째 조회 (Cache Hit -> Redis 조회)
      Product result = productService.getProduct(productId);

      // assert 2: DB는 비었지만 결과가 나와야 함
      assertAll("상세 조회 캐시 검증",
          () -> assertEquals(savedProduct.getId(), result.getId(), "ID가 일치해야 함"),
          () -> assertEquals("상세보기 상품", result.getName(), "캐시된 상품명 일치 확인")
      );
    }
  }
}
