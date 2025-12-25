package com.loopers.event;

import java.time.LocalDateTime;

public record LikeCountEvent(
    String eventId,
    Long productId,
    int currentLikeCount,
    LocalDateTime createdAt
) {

  public static LikeCountEvent of(String eventId, Long productId, int currentLikeCount) {
    return new LikeCountEvent(
        eventId,
        productId,
        currentLikeCount,
        LocalDateTime.now()
    );
  }
}
