package com.loopers.application.monitoring.port;

public interface MonitoringService {

  void incrementMetric(String name, String tag);

  void sendCriticalAlert(String message, Throwable t);
}
