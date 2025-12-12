package com.loopers.application.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.infrastructure.pg.PgClient;
import com.loopers.infrastructure.pg.PgV1Dto.PgDetail;
import com.loopers.infrastructure.pg.PgV1Dto.PgOrderResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRecoveryScheduler {

  private final PaymentRepository paymentRepository;
  private final PgClient pgClient;

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void recover() {
    LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(5);
    List<Payment> stuckPayments = paymentRepository.findAllByStatusAndCreatedAtBefore(
        PaymentStatus.READY, timeLimit
    );

    for (Payment payment : stuckPayments) {
      try {
        PgOrderResponse response = pgClient.getTransactionsByOrder(
            payment.getUserId(), String.valueOf(payment.getOrderId())
        );

        if (response.transactions() != null && !response.transactions().isEmpty()) {
          PgDetail detail = response.transactions().get(0);

          if ("SUCCESS".equals(detail.status())) {
            payment.completePayment();
          } else if ("FAIL".equals(detail.status())) {
            payment.failPayment();
          }
        } else {
          payment.failPayment();
        }
      } catch (Exception e) {
      }
    }
  }
}
