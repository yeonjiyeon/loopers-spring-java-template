package com.loopers.infrastructure.pg;

import com.loopers.infrastructure.pg.PgV1Dto.PgApiResponse;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveRequest;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveResponse;
import com.loopers.infrastructure.pg.PgV1Dto.PgOrderResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pg-client", url = "http://localhost:8082")
public interface PgClient {

  @CircuitBreaker(name = "pg-client")
  @Retry(name = "pg-client")
  @PostMapping("/api/v1/payments")
  PgApiResponse<PgApproveResponse> requestPayment(
      @RequestHeader("X-USER-ID") Long userId,
      @RequestBody PgApproveRequest request
  );

  @GetMapping("/api/v1/payments")
  PgOrderResponse getTransactionsByOrder(
      @RequestHeader("X-USER-ID") Long userId,
      @RequestParam("orderId") String orderId
  );
}
