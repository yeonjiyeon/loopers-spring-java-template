package com.loopers.application.product;

import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;

public record ProductInfo(
    Long id,
    String name,
    Money price,
    String brandName
) {
  public static ProductInfo from(Product product) {
    return new ProductInfo(
        product.getId(),
        product.getName(),
        product.getPrice(),
        null
    );
  }

  public static ProductInfo from(Product product, String brandName) {
    return new ProductInfo(
        product.getId(),
        product.getName(),
        product.getPrice(),
        brandName
    );
  }
}
