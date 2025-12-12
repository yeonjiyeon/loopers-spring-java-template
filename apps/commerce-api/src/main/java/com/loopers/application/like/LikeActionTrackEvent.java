package com.loopers.application.like;

import com.loopers.domain.event.UserActionTrackEvent;
import java.time.ZonedDateTime;
import java.util.Map;

public record LikeActionTrackEvent(
    Long userId,
    Long productId,
    String action,

    ZonedDateTime eventTime,
    Map<String, Object> properties

) implements UserActionTrackEvent {

  public LikeActionTrackEvent(Long userId, Long productId, String action) {
    this(userId, productId, action, ZonedDateTime.now(), Map.of());
  }

  @Override
  public String eventType() {
    return "LIKE_ACTION";
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }
}
