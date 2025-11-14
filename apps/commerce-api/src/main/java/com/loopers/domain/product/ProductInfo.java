package com.loopers.domain.product;

public record ProductInfo(
    Long id,
    String name,
    long price
) {
  public static ProductInfo from(Product product) {
    return new ProductInfo(
        product.getId(),
        product.getName(),
        product.getPrice()
    );
  }
}
