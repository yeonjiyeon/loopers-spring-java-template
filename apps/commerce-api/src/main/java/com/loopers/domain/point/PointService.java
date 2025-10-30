package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PointService {

    private final PointRepository pointRepository;


    @Transactional(readOnly = true)
    public Point findPointByUserId(String userId) {
        return pointRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public Point chargePoint(String userId, Long chargeAmount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트를 충전할수 없는 사용자입니다."));
        point.charge(chargeAmount);
        return pointRepository.save(point);
    }


}
