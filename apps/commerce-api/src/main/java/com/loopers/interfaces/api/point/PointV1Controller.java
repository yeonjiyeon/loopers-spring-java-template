package com.loopers.interfaces.api.point;


import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {
    private final PointFacade pointFacade;

    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ApiResponse<PointV1Dto.PointResponse> getUserPoints(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }
        Long currentPoint = pointFacade.getCurrentPoint(userId);
        PointV1Dto.PointResponse response = PointV1Dto.PointResponse.from(currentPoint);
        return ApiResponse.success(response);
    }
}
