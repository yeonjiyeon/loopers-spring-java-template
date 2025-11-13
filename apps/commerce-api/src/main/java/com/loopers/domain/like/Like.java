package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "like")
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
}
