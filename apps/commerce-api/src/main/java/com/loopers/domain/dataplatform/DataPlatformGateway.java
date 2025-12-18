package com.loopers.domain.dataplatform;

public interface DataPlatformGateway {

  void sendPaymentData(Long orderId, Long paymentId);
}
