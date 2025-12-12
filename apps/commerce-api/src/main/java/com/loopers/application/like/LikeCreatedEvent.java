package com.loopers.application.like;

import com.loopers.domain.event.DomainEvent;

public record LikeCreatedEvent(
    long productId,
    int increment
) implements DomainEvent {

}
