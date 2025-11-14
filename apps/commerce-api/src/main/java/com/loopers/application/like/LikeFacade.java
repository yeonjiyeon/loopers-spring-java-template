package com.loopers.application.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeFacade {

  private final UserService userService;
  private final ProductService productService;
  private final LikeService likeService;

  public LikeInfo like(long userId, long productId) {
    Like like = likeService.Like(userId, productId);
    long totalLikes = likeService.countLikesByProductId(productId);
    return LikeInfo.from(like, totalLikes);
  }

  public long unLike(long userId, long productId) {
   likeService.unLike(userId, productId);
    return likeService.countLikesByProductId(productId);
  }
}
