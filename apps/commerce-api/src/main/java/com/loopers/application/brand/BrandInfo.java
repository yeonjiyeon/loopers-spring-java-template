package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.application.product.ProductInfo;
import org.springframework.data.domain.Page;

public record BrandInfo(
    Long brandId,
    String brandName,
    String brandDescription,

    Page<ProductInfo> products
) {

  public static BrandInfo from(Brand brand, Page<Product> products) {

    Page<ProductInfo> productInfos = products.map(ProductInfo::from);

    return new BrandInfo(
        brand.getId(),
        brand.getName(),
        brand.getDescription(),
        productInfos
    );
  }
}
