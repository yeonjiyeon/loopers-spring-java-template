package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.domain.rank.RankingService;
import com.loopers.event.LikeCountEvent;
import com.loopers.event.ProductStockEvent;
import com.loopers.event.ProductViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
  public void consumeLikeCount(ConsumerRecord<String, LikeCountEvent> record, Acknowledgment ack) {
    try {
      metricsService.processLikeCountEvent(record.value());
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
      ack.acknowledge();
    } catch (Exception e) {
      log.error("조회수 메트릭 처리 실패: {}", event.eventId(), e);
    }
  }

  @KafkaListener(
      topics = "catalog-events",
      groupId = "metrics-group"
  )
  public void consumeSalesCount(ProductStockEvent event, Acknowledgment ack) {
    try {
      metricsService.processSalesCountEvent(event);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("판매량 메트릭 처리 실패: {}", event.eventId(), e);
    }
  }
}
