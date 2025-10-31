package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointService {

  private final UserRepository userRepository;

  public Integer getPoint(String userId) {
    return userRepository.findByUserId(userId)
        .map(User::getPoint)
        .orElse(null);
  }

  public PointResponse charge(String userId, int amount) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다."));
    int chargePoint = user.chargePoint(amount);

    return PointResponse.from(chargePoint);
  }
}
