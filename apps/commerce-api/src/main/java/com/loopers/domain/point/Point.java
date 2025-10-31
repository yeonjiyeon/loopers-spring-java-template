package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "point")
@Getter
public class Point extends BaseEntity {

    private String userId;

    private Long amount;

    protected Point() {}

    public Point(String userId, Long amount) {
        this.userId = requireValidUserId(userId);
        this.amount = amount;
    }

    String requireValidUserId(String userId) {
        if(userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 비어있을 수 없습니다.");
        }
        return userId;
    }

    public void charge(Long chargeAmount) {
        if (chargeAmount == null || chargeAmount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0원 이하로 포인트를 충전 할수 없습니다.");
        }
        this.amount += chargeAmount;
        new Point(this.userId, this.amount);
    }
}
