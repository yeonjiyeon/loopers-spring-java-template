package com.loopers.domain.like;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  public Like createLike(Like like) {
    return likeRepository.findByUserIdAndProductId(like.getUserId(), like.getProductId())
        .orElseGet(() -> likeRepository.save(like));
  }

  public void deleteLike(Long userId, Long productId) {
    likeRepository.deleteByUserIdAndProductId(userId, productId);
  }
}
