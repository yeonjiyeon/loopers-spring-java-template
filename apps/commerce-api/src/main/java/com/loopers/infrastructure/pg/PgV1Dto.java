package com.loopers.infrastructure.pg;

import java.util.List;

public class PgV1Dto {

  public record PgApiResponse<T>(
      String result,
      T data,
      String message
  ) {}

  public record PgApproveRequest(
      String orderId,
      String cardType,
      String cardNo,
      Long amount,
      String callbackUrl
  ) {}

  public record PgApproveResponse(
      String transactionKey,
      String status,
      String reason
  ) {}

  public record PgOrderResponse(
      List<PgDetail> transactions
  ) {}

  public record PgDetail(
      String transactionKey,
      String paymentKey,
      String orderId,
      String status
  ) {}
}
