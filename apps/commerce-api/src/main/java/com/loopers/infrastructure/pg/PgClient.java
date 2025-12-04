package com.loopers.infrastructure.pg;
import com.loopers.infrastructure.pg.PgV1Dto.PgApiResponse;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveRequest;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "pg-client", url = "http://localhost:8082")
public interface PgClient {
  @PostMapping("/api/v1/payments")
  PgApiResponse<PgApproveResponse> requestPayment(
      @RequestHeader("X-USER-ID") Long userId,
      @RequestBody PgApproveRequest request
  );
}
