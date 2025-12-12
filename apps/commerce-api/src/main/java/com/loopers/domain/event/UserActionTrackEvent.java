package com.loopers.domain.event;

import java.time.ZonedDateTime;
import java.util.Map;

public interface UserActionTrackEvent {
  Long userId();
  String eventType();
  ZonedDateTime eventTime();
  Map<String, Object> getProperties();
}
