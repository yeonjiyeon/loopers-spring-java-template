package com.loopers.domain.product;

import com.loopers.domain.order.OrderItem;
import com.loopers.core.cache.RedisCacheHandler;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ProductService {

  private final ProductRepository productRepository;

  private final RedisCacheHandler redisCacheHandler;

  public Page<Product> getProducts(Pageable pageable) {
    String key = makeCacheKey("product:list", pageable);
    return redisCacheHandler.getOrLoad(
        key,
        Duration.ofMinutes(5),
        Page.class,
        () -> productRepository.findAll(pageable)
    );
  }

  @Transactional
  public Product getProduct(Long id) {
    String key = "product:detail:" + id;
    return redisCacheHandler.getOrLoad(
        key,
        Duration.ofMinutes(10),
        Product.class,
        () -> productRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."))
    );
  }

  public Page<Product> getProductsByBrandId(Long brandId, Pageable pageable) {
    String key = makeCacheKey("product:list:brand:" + brandId, pageable);
    return redisCacheHandler.getOrLoad(
        key,
        Duration.ofMinutes(5),
        Page.class,
        () -> productRepository.findByBrandId(brandId, pageable)
    );
  }


  public List<Product> getProducts(List<Long> productIds) {
    return productIds.stream()
        .map(this::getProduct)
        .toList();
  }

  public void deductStock(List<Product> products, List<OrderItem> orderItems) {

    Map<Long, Integer> quantityMap = orderItems.stream()
        .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));

    for (Product product : products) {
      int quantityToDeduct = quantityMap.get(product.getId());
      if (product.getStock() < quantityToDeduct) {
        throw new CoreException(ErrorType.BAD_REQUEST, "품절된 상품입니다.");
      }
      product.deductStock(quantityToDeduct);
    }
  }

  @Transactional
  public int increaseLikeCount(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

    return product.increaseLikeCount();
  }

  @Transactional
  public int decreaseLikeCount(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

    return product.decreaseLikeCount();
  }

  private String makeCacheKey(String prefix, Pageable pageable) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append(":page:").append(pageable.getPageNumber());
    sb.append(":size:").append(pageable.getPageSize());

    if (pageable.getSort().isSorted()) {
      pageable.getSort().forEach(order ->
          sb.append(":sort:").append(order.getProperty()).append(",").append(order.getDirection())
      );
    }
    return sb.toString();
  }

  public int getStock(Long id) {
    return productRepository.findStockById(id);
  }
}
