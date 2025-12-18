package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class CouponService {

  private final CouponRepository couponRepository;


  public Coupon getCoupon(Long id) {
    return couponRepository.findById(id)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
  }

  @Transactional(readOnly = true)
  public long calculateDiscountAmount(
      Long couponId,
      long totalOrderAmount
  ) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

    if (!coupon.canUse()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
    }

    return coupon.calculateDiscountAmount(totalOrderAmount);
  }

  @Transactional
  public void reserveCoupon(Long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

    try {
      coupon.reserve();
    } catch (CoreException e) {
      throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 예약에 실패했습니다.");
    }
  }

  @Transactional
  public void confirmCouponUsage(Long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
    coupon.confirmUse();
  }
}
