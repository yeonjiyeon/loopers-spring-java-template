package com.loopers.application.product;

import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductFacade {
  private final ProductService productService;
  private final BrandService brandService;

  public Page<ProductInfo> getProductInfo(Pageable pageable) {
    Page<Product> products = productService.getProducts(pageable);
    return products.map(product -> {
      String brandName = brandService.getBrand(product.getBrandId())
          .getName();
      return ProductInfo.from(product, brandName);
    });
  }

}
