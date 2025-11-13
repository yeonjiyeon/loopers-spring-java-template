package com.loopers.domain.metrics.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductMetricsService {
    private final ProductMetricsRepository productMetricsRepository;

    public ProductMetrics getMetricsByProductId(Long productId) {
        return productMetricsRepository.findByProductId(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당 상품의 메트릭 정보를 찾을 수 없습니다."));
    }

    public Map<Long, ProductMetrics> getMetricsMapByProductIds(Collection<Long> productIds) {
        return productMetricsRepository.findByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(ProductMetrics::getProductId, metrics -> metrics));
    }
}
