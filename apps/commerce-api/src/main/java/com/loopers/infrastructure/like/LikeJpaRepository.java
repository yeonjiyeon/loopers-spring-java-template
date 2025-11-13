package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

  Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

  void deleteByUserIdAndProductId(Long userId, Long productId);
}
