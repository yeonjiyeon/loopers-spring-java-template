package com.loopers.infrastructure;

import com.loopers.domain.event.EventHandled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventHandledRepository extends JpaRepository<EventHandled, String> {

}
