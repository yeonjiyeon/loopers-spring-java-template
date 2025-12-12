package com.loopers.application.dataplatform;

import com.loopers.application.payment.PaymentEvent.PaymentCompletedEvent;
import com.loopers.domain.dataplatform.DataPlatformGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DataPlatformEventListener {

  private final DataPlatformGateway dataPlatformGateway;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleOrderCreatedEvent(PaymentCompletedEvent event) {
    dataPlatformGateway.sendPaymentData(event.orderId(), event.paymentId());
  }
}
