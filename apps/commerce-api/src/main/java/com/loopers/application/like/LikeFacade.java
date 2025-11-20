package com.loopers.application.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeFacade {

  private final ProductService productService;
  private final LikeService likeService;

  @Transactional
  public LikeInfo like(long userId, long productId) {
    Optional<Like> existingLike = likeService.findLike(userId, productId);


    if (existingLike.isPresent()) {
      Product product = productService.getProduct(productId);
      return LikeInfo.from(existingLike.get(), product.getLikeCount());
    }

    Product product = productService.getProductWithLock(productId);
    Like newLike = likeService.save(userId, productId);
    int updatedLikeCount = productService.increaseLikeCount(product);

    return LikeInfo.from(newLike, updatedLikeCount);
  }

  @Transactional
  public int unLike(long userId, long productId) {
   likeService.unLike(userId, productId);
    Product product = productService.getProductWithLock(productId);
    return productService.decreaseLikeCount(product);
  }
}
