package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import org.springframework.data.domain.Page;

public record BrandInfo(
    // 1. Brand 엔티티 대신 필요한 필드만 추출
    Long brandId,
    String brandName,
    String brandDescription,

    // 2. Page<Product> 대신 Page<ProductInfo> 포함
    Page<ProductInfo> products
) {

  public static BrandInfo from(Brand brand, Page<Product> products) {

    // 3. Page<Product>를 Page<ProductInfo>로 변환 (핵심)
    Page<ProductInfo> productInfos = products.map(ProductInfo::from);

    return new BrandInfo(
        brand.getId(),
        brand.getName(),
        brand.getDescription(),
        productInfos
    );
  }
}
