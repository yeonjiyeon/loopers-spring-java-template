package com.loopers.domain.metrics;

import com.loopers.domain.event.EventHandled;
import com.loopers.event.LikeKafkaEvent;
import com.loopers.infrastructure.EventHandledRepository;
import com.loopers.infrastructure.ProductMetricsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductMetricsService {

  private final ProductMetricsRepository metricsRepository;
  private final EventHandledRepository eventHandledRepository;

  @Transactional
  public void processLikeEvent(LikeKafkaEvent event) {
    if (eventHandledRepository.existsById(event.eventId())) {
      return;
    }

    ProductMetrics metrics = metricsRepository.findById(event.productId())
        .orElseGet(() -> new ProductMetrics(event.productId()));

    metrics.updateLikeCount(event.currentLikeCount(), LocalDateTime.now());
    metricsRepository.save(metrics);

    eventHandledRepository.save(new EventHandled(event.eventId()));
  }
}
