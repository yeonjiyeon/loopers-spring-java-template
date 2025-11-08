package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.User;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "point")
@Getter
public class Point extends BaseEntity {
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    private User user;
    private Long amount;

    protected Point() {
    }

    private Point(User user, Long amount) {
        this.user = user;
        this.amount = amount;
    }

    public static Point create(User user, int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 포인트는 0보다 커야 합니다.");
        }

        return new Point(user, (long) amount);
    }
}
