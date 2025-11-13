package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeRepositoryImpl implements LikeRepository {

  private final LikeJpaRepository likeJpaRepository;

  @Override
  public Like save(Like like) {
    return likeJpaRepository.save(like);
  }

  @Override
  public Optional<Like> findByUserIdAndProductId(Long userId,
      Long productId) {
    return likeJpaRepository.findByUserIdAndProductId(userId, productId);
  }

  @Override
  public void deleteByUserIdAndProductId(Long userId, Long productId) {
     likeJpaRepository.deleteByUserIdAndProductId(userId, productId);
  }

}
