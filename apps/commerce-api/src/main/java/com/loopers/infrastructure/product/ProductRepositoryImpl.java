package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository productJpaRepository;

  @Override
  public Product save(Product product) {
    return productJpaRepository.save(product);
  }

  @Override
  public Page<Product> findAll(Pageable pageable) {
    return productJpaRepository.findAll(pageable);
  }

  @Override
  public Optional<Product> findById(Long id) {
    return productJpaRepository.findById(id);
  }

  @Override
  public Page<Product> findByBrandId(Long brandId, Pageable pageable) {
    return productJpaRepository.findByBrandId(brandId, pageable);
  }

  @Override
  public Optional<Product> findByIdWithLock(Long id) {
    return productJpaRepository.findByIdWithLock(id);
  }
}
