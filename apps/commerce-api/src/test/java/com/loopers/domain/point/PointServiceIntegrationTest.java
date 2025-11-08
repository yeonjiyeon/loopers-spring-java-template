package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    //존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void throwsExceptionWhenChargePointWithNonExistingUserId() {
        // arrange
        String nonExistingUserId = "nonexist";
        int chargeAmount = 1000;

        // act & assert
        assertThrows(CoreException.class, () -> pointService.chargePoint(nonExistingUserId, chargeAmount));
    }

}
