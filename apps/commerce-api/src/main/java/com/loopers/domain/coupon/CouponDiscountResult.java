package com.loopers.domain.coupon;

public record CouponDiscountResult(
    long discountAmount,
    Long usedCouponId
) {

}
