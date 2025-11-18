package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import jakarta.validation.constraints.NotNull;

public class PointV1Dto {

  public record PointResponse(Point point) {

    public static PointResponse from(Point chargePoint) {
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
