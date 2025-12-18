package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.event.LikeKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventConsumer {

  private final ProductMetricsService metricsService;

  @KafkaListener(
      topics = "catalog-events",
      groupId = "metrics-group"
  )
  public void onMessage(ConsumerRecord<String, LikeKafkaEvent> record, Acknowledgment ack) {
    try {
      log.info("이벤트 수신: {}", record.value().eventId());

      metricsService.processLikeEvent(record.value());

      ack.acknowledge();

    } catch (Exception e) {
      log.error("이벤트 처리 실패, 다음 재시도를 위해 Ack를 하지 않음: {}", e.getMessage());
    }
  }
}
