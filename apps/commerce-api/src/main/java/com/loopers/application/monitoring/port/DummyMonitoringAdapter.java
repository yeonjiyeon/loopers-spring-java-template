package com.loopers.application.monitoring.port;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyMonitoringAdapter implements MonitoringService {
  @Override
  public void incrementMetric(String name, String tag) {
    log.info("[METRIC_DUMMY] Increment | Name: {} | Tag: {}", name, tag);
  }

  @Override
  public void sendCriticalAlert(String message, Throwable t) {
    log.error("[ALERT_DUMMY] CRITICAL ALERT: {}", message, t);
  }
}
