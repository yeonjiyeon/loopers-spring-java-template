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

  @Transactional
  public Payment createPendingPayment(User user, Order order, String cardType, String cardNo) {
    Payment payment = new Payment(
        order.getId(),
        user.getId(),
        order.getTotalAmount(),
        cardType,
        cardNo
    );

    return paymentRepository.save(payment);
  }
}
