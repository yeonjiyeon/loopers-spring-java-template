package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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

  public Product getProduct(Long id) {
    return productRepository.findById(id).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
  }
}
