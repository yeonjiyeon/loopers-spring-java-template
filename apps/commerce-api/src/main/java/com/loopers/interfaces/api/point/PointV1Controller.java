package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ApiResponse<PointResponse> getPoints(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
    if (userId == null || userId.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "필수 헤더인 X-USER-ID가 없거나 유효하지 않습니다.");
    }
    Integer point = pointService.getPoint(userId);
    PointResponse response = new PointResponse(point);
    return ApiResponse.success(response);
  }
}
