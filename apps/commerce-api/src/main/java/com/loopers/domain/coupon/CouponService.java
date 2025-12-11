package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponService {

  private final CouponRepository couponRepository;


  public Coupon getCoupon(Long id) {
    return couponRepository.findById(id)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
  }

  @Transactional
  public CouponDiscountResult useCouponAndCalculateDiscount(
      Long couponId,
      long totalOrderAmount
  ) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

    try {
      coupon.use();
    } catch (CoreException e) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이미 사용되었거나 사용할 수 없는 쿠폰입니다.");
    }

    long discountAmount = coupon.calculateDiscountAmount(totalOrderAmount);

    return new CouponDiscountResult(discountAmount, couponId);
  }
}
