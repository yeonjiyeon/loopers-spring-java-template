package com.loopers.infrastructure.pg;

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
}
