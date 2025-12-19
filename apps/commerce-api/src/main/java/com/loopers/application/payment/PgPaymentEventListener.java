package com.loopers.application.payment;

import com.loopers.application.order.event.OrderCreatedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentRequestFailedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentRequestedEvent;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentProcessor;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PgPaymentEventListener {

  private final List<PaymentProcessor> paymentProcessors;
  private final UserService userService;
  private final ApplicationEventPublisher eventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleOrderCreatedEvent(OrderCreatedEvent event) {

    if (event.paymentType() != PaymentType.PG) {
      return;
    }

    User user = userService.findById(event.userId())
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

    PaymentProcessor processor = paymentProcessors.stream()
        .filter(p -> p.supports(event.paymentType()))
        .findFirst()
        .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "PG 결제 프로세서를 찾을 수 없습니다."));

    Map<String, Object> paymentDetails = Map.of(
        "cardType", event.cardType(),
        "cardNo", event.cardNo()
    );

    try {
      Payment payment = processor.process(
          event.orderId(),
          user,
          event.finalAmount(),
          paymentDetails
      );

      eventPublisher.publishEvent(new PaymentRequestedEvent(
          event.orderId(), payment.getId(), event.couponId()));

    } catch (Exception e) {
      eventPublisher.publishEvent(new PaymentRequestFailedEvent(
          event.orderId(),
          event.couponId(),
          e.getMessage()
      ));
    }
  }
}
