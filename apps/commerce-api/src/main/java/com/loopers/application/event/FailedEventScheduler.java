package com.loopers.application.event;

import com.loopers.domain.event.DomainEvent;

public interface FailedEventScheduler {

  <T extends DomainEvent> void scheduleRetry(T event, String reason);
}
