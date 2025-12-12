package com.loopers.application.coupon;

import com.loopers.application.payment.PaymentEvent.PaymentCompletedEvent;
import com.loopers.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponUsageEventListener {
  private final CouponService couponService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional
  public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {

    if (event.couponId() != null) {
      couponService.confirmCouponUsage(event.couponId());
    }
  }
}
