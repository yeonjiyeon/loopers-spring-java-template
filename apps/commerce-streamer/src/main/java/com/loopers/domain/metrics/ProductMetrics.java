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

  private int likeCount = 0;
  private int viewCount = 0;
  private int salesCount = 0;

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

  public void incrementViewCount() {
    this.viewCount += 1;
    this.updatedAt = LocalDateTime.now();
  }

  public void addSalesCount(int quantity) {
    this.salesCount += quantity;
    this.updatedAt = LocalDateTime.now();
  }
}
