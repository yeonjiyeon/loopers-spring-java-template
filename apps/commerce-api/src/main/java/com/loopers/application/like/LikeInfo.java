package com.loopers.application.like;

import com.loopers.domain.like.Like;

public record LikeInfo(
    Long id,
    Long userId,
    Long productId,
    long totalLikes
) {

  public static LikeInfo from(Like like, long totalLikes) {
    return new LikeInfo(
        like.getId(),
        like.getUserId(),
        like.getProductId(),
        totalLikes
    );
  }
}
