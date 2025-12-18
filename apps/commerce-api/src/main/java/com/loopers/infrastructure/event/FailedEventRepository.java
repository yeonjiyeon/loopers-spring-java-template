package com.loopers.infrastructure.event;

import com.loopers.domain.event.FailedEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {

  List<FailedEvent> findByRetryCountLessThan(int maxRetries);
}
