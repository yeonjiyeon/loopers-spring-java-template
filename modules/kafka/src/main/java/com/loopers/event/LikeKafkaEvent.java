package com.loopers.event;

public record LikeKafkaEvent(
   String eventId,
    Long productId,
    int currentLikeCount
) {

}
