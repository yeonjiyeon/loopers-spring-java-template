package com.loopers.domain.payment;

import com.loopers.domain.user.User;
import java.util.Map;

public interface PaymentProcessor {

  Payment process(Long orderId, User user, long finalAmount, Map<String, Object> paymentDetails);

  boolean supports(PaymentType paymentType);
}


