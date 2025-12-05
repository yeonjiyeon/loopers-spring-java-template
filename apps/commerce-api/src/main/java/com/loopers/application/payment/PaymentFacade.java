package com.loopers.application.payment;

import com.loopers.domain.money.Money;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentExecutor;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

  private final PaymentService paymentService;
  private final PaymentExecutor paymentExecutor;

  public Payment processPaymentRequest(PaymentCommand.CreatePayment command) {

    return paymentService.findValidPayment(command.orderId())
        .orElseGet(() -> {
          Payment newPayment = paymentService.createPendingPayment(command.userId(),
              command.orderId(),
              new Money(command.amount()),
              command.cardType(),
              command.cardNo());

          try {
            String pgTxnId = paymentExecutor.execute(newPayment);

            paymentService.registerPgToken(newPayment, pgTxnId);

            return newPayment;
          } catch (Exception e) {
            paymentService.failPayment(newPayment);
            throw e;
          }
        });
  }


  public void handlePaymentCallback(String pgTxnId, boolean isSuccess) {
    Payment payment = paymentService.getPaymentByPgTxnId(pgTxnId);

    if (isSuccess) {
      paymentService.completePayment(payment);
    } else {
      paymentService.failPayment(payment);
    }
  }
}
