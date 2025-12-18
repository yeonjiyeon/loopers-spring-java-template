package com.loopers.application.payment;

import static com.loopers.domain.user.QUser.user;

import com.loopers.domain.money.Money;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentProcessor;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPaymentProcessor implements PaymentProcessor {

  private final PointService pointService;
  private final PaymentService paymentService;

  @Override
  public Payment process(Long orderId, User user, long finalAmount, Map<String, Object> paymentDetails) {
    pointService.deductPoint(user, finalAmount);

    return paymentService.createPointPaymentAndComplete(
        orderId,
        user.getId(),
        new Money(finalAmount));
  }

  @Override
  public boolean supports(PaymentType paymentType) {
    return paymentType == PaymentType.POINT;
  }
}
