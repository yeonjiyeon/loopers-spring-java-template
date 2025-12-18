package com.loopers.infrastructure.event;

import com.loopers.domain.event.OutboxEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

  List<OutboxEvent> findAllByPublishedFalse();

  Optional<OutboxEvent> findFirstByAggregateIdAndEventTypeAndPublishedFalseOrderByCreatedAtDesc(String aggregateId, String eventType);
}
