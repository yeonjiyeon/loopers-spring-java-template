package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointService {
    private final PointJpaRepository pointJpaRepository;
    private final UserService userService;

    @Transactional
    public Long chargePoint(String userId, int amount) {
        User user = userService.findByUserIdForUpdate(userId).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Point point = Point.create(user, amount);

        user.setCurrentPoint(user.getCurrentPoint() + amount);
        pointJpaRepository.save(point);

        return user.getCurrentPoint();
    }
}
