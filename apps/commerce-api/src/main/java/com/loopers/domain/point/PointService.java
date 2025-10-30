package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
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

}
