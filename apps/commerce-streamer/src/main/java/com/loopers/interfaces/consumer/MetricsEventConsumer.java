package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.domain.rank.RankingService;
import com.loopers.event.LikeCountEvent;
import com.loopers.event.ProductStockEvent;
import com.loopers.event.ProductViewEvent;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsEventConsumer {

  private final ProductMetricsService metricsService;
  private final RankingService rankingService;

  @KafkaListener(
      topics = "catalog-events",
      groupId = "metrics-group"
  )
  public void consumeLikeCount(LikeCountEvent event, Acknowledgment ack) {
    try {
      metricsService.processLikeCountEvent(event);
      rankingService.addScore(event.productId(), 1.0, 0.2, event.createdAt());
      ack.acknowledge();
    } catch (Exception e) {
      log.error("좋아요 메트릭 처리 실패: {}", e.getMessage());
    }
  }

  @KafkaListener(
      topics = "catalog-events",
      groupId = "metrics-group"
  )
  public void consumeProductView(ProductViewEvent event, Acknowledgment ack) {
    try {
      metricsService.processProductViewEvent(event);
      rankingService.addScore(event.productId(), 1.0, 0.1, event.createdAt());
      ack.acknowledge();
    } catch (Exception e) {
      log.error("조회수 메트릭 처리 실패: {}", event.eventId(), e);
    }
  }

  @KafkaListener(
      topics = "catalog-events",
      groupId = "metrics-group",
      containerFactory = "batchFactory"
  )
  public void consumeSalesCount(List<ProductStockEvent> events, Acknowledgment ack) {
    try {
      Map<String, Map<Long, Double>> rankingUpdates = new HashMap<>();

      for (ProductStockEvent event : events) {
        metricsService.processSalesCountEvent(event);

        String dateKey = "ranking:all:" + event.createdAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        double orderScore = (event.price() * event.sellQuantity()) * 0.6;

        rankingUpdates.computeIfAbsent(dateKey, k -> new HashMap<>())
            .merge(event.productId(), orderScore, Double::sum);
      }

      rankingService.addOrderScoresBatch(rankingUpdates);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("판매량 메트릭 처리 실패", e);
    }
  }
}
