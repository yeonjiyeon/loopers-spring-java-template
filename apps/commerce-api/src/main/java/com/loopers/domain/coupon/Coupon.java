package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

  @Version
  private long version;

  @Column(name = "ref_user_id", nullable = false)
  private long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponType type;

  private long discountValue;

  private boolean used;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private CouponStatus status;

  public Coupon(long userId, CouponType type, long discountValue) {
    if (type == CouponType.PERCENTAGE && (discountValue < 0 || discountValue > 100)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "할인율은 0~100% 사이여야 합니다.");
    }
    this.userId = userId;
    this.type = type;
    this.discountValue = discountValue;
    this.used = false;
  }

  public boolean isUsed() {
    return used;
  }

  public long calculateDiscountAmount(long totalOrderAmount) {
    return this.type.calculate(totalOrderAmount, this.discountValue);
  }

  public void use() {
    if (this.used) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이미 사용된 쿠폰입니다.");
    }
    this.used = true;
  }

  public void reserve() {
    if (this.status != CouponStatus.ISSUED) {
      throw new CoreException(ErrorType.BAD_REQUEST,
          "현재 상태(" + this.status + ")에서는 쿠폰을 예약할 수 없습니다.");
    }

    this.status = CouponStatus.RESERVED;
  }

  public void confirmUse() {
    if (this.status != CouponStatus.RESERVED) {
      throw new CoreException(ErrorType.BAD_REQUEST,
          "예약 상태가 아니므로 사용을 확정할 수 없습니다: " + this.status);
    }
    this.status = CouponStatus.USED;
  }

  public boolean canUse() {
    return  this.status == CouponStatus.ISSUED;
  }
}
