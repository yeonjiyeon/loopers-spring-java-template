package com.loopers.application.order.event;

import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.user.User;

public record OrderCreatedEvent(
    Long orderId,
    User user,
    long finalAmount,
    PaymentType paymentType,
    String cardType,
    String cardNo,
    Long couponId
) {}
