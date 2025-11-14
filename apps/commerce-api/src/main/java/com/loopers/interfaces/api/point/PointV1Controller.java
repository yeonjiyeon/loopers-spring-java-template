package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.ChargePointsRequest;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/point")
public class PointV1Controller implements PointV1ApiSpec {

  private final PointService pointService;

  @GetMapping()
  @Override
  public ApiResponse<PointResponse> getPoint(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
    if (userId == null || userId.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "필수 헤더인 X-USER-ID가 없거나 유효하지 않습니다.");
    }
    Point point = pointService.getPoint(userId);
    PointResponse response = new PointResponse(point);
    return ApiResponse.success(response);
  }

  @PostMapping()
  @Override
  public ApiResponse<PointResponse> chargePoint(@Valid @RequestBody ChargePointsRequest request) {
    PointResponse response = pointService.charge(request.userId(), request.point());
    return ApiResponse.success(response);
  }


}
