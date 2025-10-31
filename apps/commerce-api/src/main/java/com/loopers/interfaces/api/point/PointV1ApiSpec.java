package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

public interface PointV1ApiSpec {

  @Operation(
      summary = "포인트 조회",
      description = "헤더의 ID로 보유 포인트를 조회합니다."
  )
  ApiResponse<PointV1Dto.PointResponse> getPoint(
      @Schema(name = "X-USER-ID", description = "조회할 사용자 ID (헤더)")
          String userId
  );

    @Operation(
      summary = "포인트 충전",
      description = "ID에 해당하는 사용자의 포인트를 충전합니다."
  )
  ApiResponse<PointV1Dto.PointResponse> chargePoint(
        PointV1Dto.ChargePointsRequest request
  );
}
