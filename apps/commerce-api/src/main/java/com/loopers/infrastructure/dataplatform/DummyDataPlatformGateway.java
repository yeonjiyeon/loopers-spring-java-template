package com.loopers.infrastructure.dataplatform;

import com.loopers.domain.dataplatform.DataPlatformGateway;
import org.springframework.stereotype.Component;

@Component
public class DummyDataPlatformGateway implements DataPlatformGateway {

  @Override
  public void sendPaymentData(Long orderId, Long paymentId) {
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

  }
}
