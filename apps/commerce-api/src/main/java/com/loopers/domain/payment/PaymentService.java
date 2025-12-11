package com.loopers.domain.payment;

import com.loopers.domain.money.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public Optional<Payment> findValidPayment(Long orderId) {
    return paymentRepository.findByOrderId(orderId)
        .filter(Payment::isProcessingOrCompleted);
  }

  @Transactional
  public Payment createPendingPayment(Long userId, Long orderId, Money amount, CardType cardType, String cardNo) {
    Payment payment = new Payment(
        orderId,
        userId,
        amount,
        PaymentType.PG,
        cardType,
        cardNo
    );
    return paymentRepository.save(payment);
  }

  @Transactional
  public Payment createPointPaymentAndComplete(Long userId, Long orderId, Money amount) {

    Payment payment = new Payment(
        orderId,
        userId,
        amount,
        PaymentType.POINT,
        null,
        null
    );
    paymentRepository.save(payment);

    completePayment(payment);

    return payment;
  }

  @Transactional
  public void registerPgToken(Payment payment, String pgTxnId) {
    payment.setPgTxnId(pgTxnId);
  }

  @Transactional
  public void failPayment(Payment payment) {
    payment.failPayment();
  }

  public Payment getPaymentByPgTxnId(String pgTxnId) {
    return paymentRepository.findByPgTxnId(pgTxnId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
  }

  @Transactional
  public void completePayment(Payment payment) {
    payment.completePayment();
  }

}
