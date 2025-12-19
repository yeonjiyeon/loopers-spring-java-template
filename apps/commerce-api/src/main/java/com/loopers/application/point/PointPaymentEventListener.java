package com.loopers.application.point;

import com.loopers.application.order.event.OrderCreatedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentCompletedEvent;
import com.loopers.application.payment.PaymentEvent.PaymentRequestFailedEvent;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.OrderStatus;
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
public class PointPaymentEventListener {

  private final List<PaymentProcessor> paymentProcessors;
  private final OrderService orderService;
  private final UserService userService;
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

    User user = userService.findById(event.userId())
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다."));

    try {
      Payment payment = processor.process(
          event.orderId(),
          user,
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
