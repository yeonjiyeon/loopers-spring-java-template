package com.loopers.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.payment.Payment;
import com.loopers.infrastructure.pg.PgV1Dto.PgApiResponse;
import com.loopers.infrastructure.pg.PgV1Dto.PgApproveResponse;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoopersPgExecutorTest {

  @Mock
  private PgClient pgClient;

  @InjectMocks
  private LoopersPgExecutor loopersPgExecutor;

  @Test
  @DisplayName("PG 승인 요청이 성공하면 트랜잭션 키를 반환한다")
  void execute_success() {
    // given
    Payment mockPayment = org.mockito.Mockito.mock(Payment.class);
    given(mockPayment.getTransactionId()).willReturn("ORDER-123");
    given(mockPayment.getCardType()).willReturn(com.loopers.domain.payment.CardType.SAMSUNG);
    given(mockPayment.getCardNo()).willReturn("1234-5678");
    given(mockPayment.getAmount()).willReturn(new com.loopers.domain.money.Money(1000L));
    given(mockPayment.getUserId()).willReturn(1L);

    PgApproveResponse approveData = new PgApproveResponse("TX-KEY-001", "DONE", "OK");
    PgApiResponse<PgApproveResponse> successResponse = new PgApiResponse<>("SUCCESS", approveData, "성공");

    given(pgClient.requestPayment(eq(1L), any())).willReturn(successResponse);

    // when
    String resultTransactionKey = loopersPgExecutor.execute(mockPayment);

    // then
    assertEquals("TX-KEY-001", resultTransactionKey);
  }

  @Test
  @DisplayName("PG 응답이 FAIL이면 CoreException을 던진다")
  void execute_fail() {
    // given
    Payment mockPayment = org.mockito.Mockito.mock(Payment.class);
    given(mockPayment.getTransactionId()).willReturn("ORDER-123");
    given(mockPayment.getCardType()).willReturn(com.loopers.domain.payment.CardType.SAMSUNG);
    given(mockPayment.getCardNo()).willReturn("1234-5678");
    given(mockPayment.getAmount()).willReturn(new com.loopers.domain.money.Money(1000L));
    given(mockPayment.getUserId()).willReturn(1L);

    PgApiResponse<PgApproveResponse> failResponse = new PgApiResponse<>("FAIL", null, "잔액 부족");
    given(pgClient.requestPayment(eq(1L), any())).willReturn(failResponse);

    // when & then
    assertThrows(CoreException.class, () -> {
      loopersPgExecutor.execute(mockPayment);
    });
  }
}
