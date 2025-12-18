package com.loopers.application.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeFacade {

  private final ProductService productService;
  private final LikeService likeService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public LikeInfo like(long userId, long productId) {
    Optional<Like> existingLike = likeService.findLike(userId, productId);
    Product product = productService.getProduct(productId);

    if (existingLike.isPresent()) {
      return LikeInfo.from(existingLike.get(), product.getLikeCount());
    }

    Like newLike = likeService.save(userId, productId);

    eventPublisher.publishEvent(new LikeCreatedEvent(productId, 1));

    eventPublisher.publishEvent(new LikeActionTrackEvent(
        userId,
        productId,
        "LIKE"
    ));

    return LikeInfo.from(newLike, product.getLikeCount());
  }

  @Transactional
  public int unLike(long userId, long productId) {

    likeService.unLike(userId, productId);

    eventPublisher.publishEvent(new LikeCreatedEvent(productId, -1));

    eventPublisher.publishEvent(new LikeActionTrackEvent(
        userId,
        productId,
        "UNLIKE"
    ));

    return productService.getProduct(productId).getLikeCount();

  }

}
