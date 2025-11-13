package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductService {

  private final ProductRepository productRepository;


  public Page<Product> getProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }
}
