package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_likes_user_product",
        columnNames = {"userId", "productId"}
    )
})
public class Like extends BaseEntity {
  private Long userId;
  private Long productId;

  protected Like() {}

  public Like(Long userId, Long productId) {
    super();
    if (userId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST);
    }
    if (productId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST);
    }
    this.userId = userId;
    this.productId = productId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getProductId() {
    return productId;
  }
}
