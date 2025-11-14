package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  public Like Like(long userId, long productId) {
    return likeRepository.findByUserIdAndProductId(userId, productId)
        .orElseGet(() -> likeRepository.save(new Like(userId, productId)));
  }

  public void unLike(Long userId, Long productId) {
    likeRepository.deleteByUserIdAndProductId(userId, productId);
  }

  public long countLikesByProductId(Long productId) {
    return likeRepository.countByProductId(productId);
  }
}
