package com.loopers.event;

public record LikeCountEvent(
   String eventId,
    Long productId,
    int currentLikeCount
) {

}
