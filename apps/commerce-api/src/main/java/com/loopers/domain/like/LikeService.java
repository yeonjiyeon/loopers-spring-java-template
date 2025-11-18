package com.loopers.domain.like;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  public Like save(long userId, long productId) {
    return likeRepository.save(new Like(userId, productId));
  }

  public Optional<Like> findLike(long userId, long productId) {
    return likeRepository.findByUserIdAndProductId(userId, productId);
  }

  public void unLike(Long userId, Long productId) {
    likeRepository.deleteByUserIdAndProductId(userId, productId);
  }
}
