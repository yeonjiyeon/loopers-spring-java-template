package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.money.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

  @Column(name = "ref_user_id", nullable = false)
  private Long userId;

  @Column(name = "ref_order_id", nullable = false)
  private Long orderId;

  @Column(name = "transaction_id", nullable = false, unique = true)
  private String transactionId;//멱등키

  @Column(name = "card_type", nullable = false)
  private String cardType;

  @Column(name = "card_no", nullable = false)
  private String cardNo;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "amount"))
  private Money amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(name = "pg_txn_id")
  private String pgTxnId;

  public Payment(Long orderId, Long userId, Money amount, String cardType, String cardNo) {
    validateConstructor(orderId, userId, amount, cardType, cardNo);

    this.orderId = orderId;
    this.userId = userId;
    this.amount = amount;
    this.cardType = cardType;
    this.cardNo = cardNo;
    this.status = PaymentStatus.READY;

    this.transactionId = UUID.randomUUID().toString();
  }

  private void validateConstructor(Long orderId, Long userId, Money amount, String cardType, String cardNo) {
    if (orderId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "주문 정보는 필수입니다.");
    }
    if (userId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "사용자 정보는 필수입니다.");
    }
    if (amount == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 필수입니다.");
    }
    if (!StringUtils.hasText(cardType)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "카드 종류는 필수입니다.");
    }
    if (!StringUtils.hasText(cardNo)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "카드 번호는 필수입니다.");
    }
  }
}
