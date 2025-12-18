package com.loopers.domain.metrics;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetrics {

  @Id
  private Long productId;

  private int likeCount;

  private LocalDateTime updatedAt;

  public ProductMetrics(Long productId) {
    this.productId = productId;
  }

  public void updateLikeCount(int newCount, LocalDateTime eventTime) {
    if (this.updatedAt != null && eventTime.isBefore(this.updatedAt)) {
      return;
    }
    this.likeCount = newCount;
    this.updatedAt = eventTime;
  }
}
