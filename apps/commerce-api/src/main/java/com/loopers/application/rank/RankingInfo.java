package com.loopers.application.rank;

import com.loopers.domain.product.Product;

public record RankingInfo(
    Long productId,
    String productName,
    Long price,
    int stock,
    int currentRank
) {

  public static RankingInfo of(Product product, int currentRank) {
    return new RankingInfo(
        product.getId(),
        product.getName(),
        product.getPrice().getValue(),
        product.getStock(),
        currentRank
    );
  }
}
