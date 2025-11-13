//package com.loopers.domain.common.vo;
//
//import com.loopers.support.error.CoreException;
//import com.loopers.support.error.ErrorType;
//
//public record Money(int amount) {
//    public Money add(Money other) {
//        return new Money(this.amount + other.amount);
//    }
//
//    public Money subtract(Money other) {
//        // defensive programming: prevent negative money amounts
//        if (this.amount < other.amount) {
//            throw new CoreException(ErrorType.BAD_REQUEST, )
//        }
//    }
//}
