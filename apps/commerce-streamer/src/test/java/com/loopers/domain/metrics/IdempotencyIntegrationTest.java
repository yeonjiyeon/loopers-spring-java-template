package com.loopers.domain.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.config.redis.RedisConfig;
import com.loopers.core.cache.RedisCacheHandler;
import com.loopers.event.ProductStockEvent;
import com.loopers.infrastructure.ProductMetricsRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(RedisConfig.class)
class IdempotencyIntegrationTest {

  @Autowired
  private ProductMetricsService metricsService;

  @Autowired
  private ProductMetricsRepository metricsRepository;

  @MockitoBean
  private RedisCacheHandler redisCacheHandler;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }


  @Test
  @DisplayName("중복 이벤트 수신 시에도 메트릭 수치는 단 한 번만 반영되어야 한다")
  void shouldHandleDuplicateEventIdempotently() {
    // Given: 동일한 ID를 가진 이벤트 준비
    Long productId = 99L;
    int salesQuantity = 2;
    int currentStock = 10;

    ProductStockEvent firstEvent = ProductStockEvent.of(productId, salesQuantity, currentStock);

    // When: 첫 번째 전송
    metricsService.processSalesCountEvent(firstEvent);

    // Then: 수치 반영 확인
    int firstResult = metricsRepository.findById(productId).get().getSalesCount();
    assertThat(firstResult).isEqualTo(2);

    // When: 동일 ID로 두 번째 전송 (Kafka 재전송 시나리오)
    metricsService.processSalesCountEvent(firstEvent);

    // Then: 수치가 4가 아니라 여전히 2여야 함 (멱등성 성공)
    int secondResult = metricsRepository.findById(productId).get().getSalesCount();
    assertThat(secondResult).isEqualTo(2);
  }
}
