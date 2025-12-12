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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
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

  @Enumerated(EnumType.STRING)
  @Column(name = "card_type", nullable = false)
  private CardType cardType;

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

  public Payment(Long orderId, Long userId, Money amount, CardType cardType, String cardNo) {
    validateConstructor(orderId, userId, amount, cardNo);

    this.orderId = orderId;
    this.userId = userId;
    this.amount = amount;
    this.cardType = cardType;
    this.cardNo = cardNo;
    this.status = PaymentStatus.READY;

    this.transactionId = UUID.randomUUID().toString();
  }

  private void validateConstructor(Long orderId, Long userId, Money amount, String cardNo) {
    if (orderId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "주문 정보는 필수입니다.");
    }
    if (userId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "사용자 정보는 필수입니다.");
    }
    if (amount == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 필수입니다.");
    }
    if (!StringUtils.hasText(cardNo)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "카드 번호는 필수입니다.");
    }
  }

  public void completePayment() {
    if (this.status == PaymentStatus.PAID || this.status == PaymentStatus.CANCELLED) {
      return;
    }
    this.status = PaymentStatus.PAID;
  }

  public void failPayment() {
    if (this.status == PaymentStatus.PAID) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이미 성공한 결제는 실패 처리할 수 없습니다.");
    }
    this.status = PaymentStatus.FAILED;
  }

  public boolean isProcessingOrCompleted() {
    return this.status == PaymentStatus.PAID || this.status == PaymentStatus.READY;
  }

  public void setPgTxnId(String pgTxnId) {
    this.pgTxnId = pgTxnId;
  }
}
