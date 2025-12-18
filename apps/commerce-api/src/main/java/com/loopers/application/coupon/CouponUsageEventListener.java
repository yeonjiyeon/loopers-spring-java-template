package com.loopers.application.coupon;

import com.loopers.application.event.FailedEventStore;
import com.loopers.application.payment.PaymentEvent.PaymentCompletedEvent;
import com.loopers.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponUsageEventListener {
  private final CouponService couponService;
  private final FailedEventStore failedEventStore;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {

    if (event.couponId() == null) return;

    try {
      couponService.confirmCouponUsage(event.couponId());

    } catch (ObjectOptimisticLockingFailureException e) {
      failedEventStore.scheduleRetry(event, "Coupon Lock Conflict on Confirmation");

    } catch (Exception e) {
      failedEventStore.scheduleRetry(event, "Coupon confirmation error: " + e.getMessage());
    }
  }
}
