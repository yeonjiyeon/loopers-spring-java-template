package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.PaymentV1Dto.CallbackRequest;
import com.loopers.interfaces.api.payment.PaymentV1Dto.CallbackResponse;
import com.loopers.interfaces.api.payment.PaymentV1Dto.PaymentRequest;
import com.loopers.interfaces.api.payment.PaymentV1Dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Payment V1 API", description = "결제/PG 관련 API 입니다.")
public interface PaymentV1ApiSpec {

  @Operation(
      summary = "PG 결제 요청",
      description = "사용자로부터 결제 요청을 받아 PG사로 결제 요청을 합니다."
  )
  ApiResponse<PaymentResponse> requestPayment(
      @RequestHeader("X-USER-ID") Long userId,
      @RequestBody PaymentRequest dto);

  @Operation(
      summary = "PG 결제 콜백 처리",
      description = "PG사에서 결제 완료 후 호출하는 Webhook 엔드포인트입니다. 결제 상태를 최종 확정합니다."
  )
  ApiResponse<CallbackResponse> handlePaymentCallback(
      @RequestBody CallbackRequest request
  );
}
