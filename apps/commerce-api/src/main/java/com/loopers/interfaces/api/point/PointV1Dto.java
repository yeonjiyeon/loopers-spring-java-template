package com.loopers.interfaces.api.point;

import jakarta.validation.constraints.NotNull;

public class PointV1Dto {

  public record PointResponse(Integer point) {

    public static PointResponse from(int chargePoint) {
      return new PointResponse(
        chargePoint
      );
    }
  }

  public record ChargePointsRequest(
      @NotNull String userId,
      @NotNull int point
  ) {}
}
