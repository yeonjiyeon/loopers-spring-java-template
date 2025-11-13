package com.loopers.domain.metrics.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductMetricsRepository {
    Optional<ProductMetrics> findByProductId(Long productId);

    Collection<ProductMetrics> findByProductIds(Collection<Long> productIds);
}
