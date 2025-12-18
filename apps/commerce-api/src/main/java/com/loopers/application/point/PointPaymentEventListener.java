package com.loopers.application.point;

import com.loopers.application.order.OrderCreatedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentCompletedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentRequestFailedEvent;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentProcessor;
import com.loopers.domain.payment.PaymentType;
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
public class PointPaymentEventListener {
  private final List<PaymentProcessor> paymentProcessors;
  private final OrderService orderService;
  private final ApplicationEventPublisher eventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleOrderCreatedEvent(OrderCreatedEvent event) {

    if (event.paymentType() != PaymentType.POINT) {
      return;
    }

    PaymentProcessor processor = paymentProcessors.stream()
        .filter(p -> p.supports(event.paymentType()))
        .findFirst()
        .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "포인트 결제 프로세서를 찾을 수 없습니다."));

    try {
      Payment payment = processor.process(
          event.orderId(),
          event.user(),
          event.finalAmount(),
          Map.of()
      );

      orderService.updateOrderStatus(event.orderId(), OrderStatus.PAYMENT_COMPLETED);

      eventPublisher.publishEvent(new PaymentCompletedEvent(
          event.orderId(),
          payment.getId(),
          true,
          event.couponId()
      ));

    } catch (CoreException e) {
      orderService.failPayment(event.orderId());
      eventPublisher.publishEvent(new PaymentRequestFailedEvent(
          event.orderId(),
          event.couponId(),
          e.getMessage()
      ));

    }
  }
}
