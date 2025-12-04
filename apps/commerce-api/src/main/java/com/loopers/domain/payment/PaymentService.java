package com.loopers.domain.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentExecutor paymentExecutor;

  @Transactional
  public Payment processPayment(User user, Order order, String cardType, String cardNo) {
    Payment payment = new Payment(
        order.getId(),
        user.getId(),
        order.getTotalAmount(),
        CardType.valueOf(cardType),
        cardNo
    );

    paymentRepository.save(payment);
    try {
      String pgTxnId = paymentExecutor.execute(payment);

      payment.completePayment(pgTxnId);

    } catch (Exception e) {
      payment.failPayment();
      throw e;
    }

    return payment;
  }
}
