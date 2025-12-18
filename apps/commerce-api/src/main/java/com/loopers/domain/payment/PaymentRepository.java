package com.loopers.domain.payment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

  Payment save(Payment payment);

  Optional<Payment> findByOrderId(Long id);

  List<Payment> findAllByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, ZonedDateTime timeLimit);

  Optional<Payment> findByPgTxnId(String pgTxnId);
}
