package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BrandFacade {
  private final BrandService brandService;
  private final ProductService productService;

  public BrandInfo getBrandInfo(long brandId, Pageable pageable) {
    Brand brand = brandService.getBrand(brandId);
    Page<Product> products = productService.getProductsByBrandId(brandId, pageable);
    return BrandInfo.from(brand, products);
  }
}
