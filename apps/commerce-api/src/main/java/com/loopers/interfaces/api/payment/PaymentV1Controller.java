package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.PaymentV1Dto.CallbackRequest;
import com.loopers.interfaces.api.payment.PaymentV1Dto.CallbackResponse;
import com.loopers.interfaces.api.payment.PaymentV1Dto.PaymentRequest;
import com.loopers.interfaces.api.payment.PaymentV1Dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

  private final PaymentFacade paymentFacade;

  @Override
  @PostMapping
  public ApiResponse<PaymentResponse> requestPayment(
      @RequestHeader("X-USER-ID") Long userId,
      @RequestBody PaymentRequest request) {
    PaymentCommand.CreatePayment command = PaymentCommand.CreatePayment.from(userId, request);
    PaymentInfo paymentInfo = PaymentInfo.from(paymentFacade.processPaymentRequest(command));
    return ApiResponse.success(PaymentResponse.from(paymentInfo));
  }

  @Override
  @PostMapping("/callback")
  public ApiResponse<CallbackResponse> handlePaymentCallback(@RequestBody CallbackRequest request) {
    try {
      boolean isSuccess = "SUCCESS".equals(request.status());

      paymentFacade.handlePaymentCallback(request.transactionKey(), isSuccess);

      return ApiResponse.success(CallbackResponse.success());

    } catch (Exception e) {

      return new ApiResponse<>(
          ApiResponse.Metadata.fail("CALLBACK_ERROR", e.getMessage()),
          CallbackResponse.fail(e.getMessage())
      );
    }
  }
}
