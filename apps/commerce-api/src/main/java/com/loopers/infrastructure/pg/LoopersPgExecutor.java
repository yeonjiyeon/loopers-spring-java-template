package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentExecutor;
import com.loopers.infrastructure.pg.PgV1Dto.PgApiResponse;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveRequest;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoopersPgExecutor implements PaymentExecutor {

  private final PgClient pgClient;

  @Override
  public String execute(Payment payment) {
    try {
      PgApproveRequest request = new PgApproveRequest(
          payment.getTransactionId(),
          payment.getCardType().name(),
          payment.getCardNo(),
          payment.getAmount().getValue(),
          "http://localhost:8080/api/v1/callback"
      );

      PgApiResponse<PgApproveResponse> responseWrapper =
          pgClient.requestPayment(payment.getUserId(), request);

      if ("SUCCESS".equals(responseWrapper.result()) && responseWrapper.data() != null) {
        return responseWrapper.data().transactionKey();
      } else {
        throw new CoreException(ErrorType.BAD_REQUEST, "결제에 실패했습니다.");
      }

    } catch (CoreException e) {
      throw e;
    } catch (Exception e) {
      throw new CoreException(ErrorType.BAD_REQUEST, "PG 연동 오류: " + e.getMessage());
    }
  }
}
