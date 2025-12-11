package com.loopers.application.payment;

import com.loopers.domain.money.Money;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentExecutor;
import com.loopers.domain.payment.PaymentProcessor;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.user.User;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PgPaymentProcessor implements PaymentProcessor {

  private final PaymentService paymentService;
  private final PaymentExecutor paymentExecutor;

  @Override
  public Payment process(Long orderId, User user, long finalAmount, Map<String, Object> paymentDetails) {

    String cardTypeStr = (String) paymentDetails.get("cardType");
    String cardNo = (String) paymentDetails.get("cardNo");

    if (cardTypeStr == null || cardNo == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "PG 결제에 필요한 카드 정보가 누락되었습니다.");
    }

    CardType cardType;
    try {
      cardType = CardType.valueOf(cardTypeStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 카드 타입입니다: " + cardTypeStr);
    }

    return paymentService.findValidPayment(orderId)
        .orElseGet(() -> {

          Payment newPayment = paymentService.createPendingPayment(user.getId(),
              orderId,
              new Money(finalAmount),
              cardType,
              cardNo
          );

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

  @Override
  public boolean supports(PaymentType paymentType) {
    return paymentType == PaymentType.PG;
  }
}
