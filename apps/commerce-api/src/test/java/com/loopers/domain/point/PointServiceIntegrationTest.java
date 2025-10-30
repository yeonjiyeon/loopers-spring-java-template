package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 조회 통합 테스트")
    @Nested
    class PointUser {

        @DisplayName("해당 ID의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnPointInfo_whenValidIdIsProvided() {
            String id = "yh45g";
            String email = "yh45g@loopers.com";
            String birth = "1994-12-05";
            String gender = "MALE";

            userRepository.save(new User(id, email, birth, gender));
            pointRepository.save(new Point(id, 0L));

            Point result = pointService.findPointByUserId(id);

            assertThat(result.getUserId()).isEqualTo(id);
            assertThat(result.getAmount()).isEqualTo(0L);
        }

        @DisplayName("회원이 존재 하지 않을 경우, null 이 반환된다.")
        @Test
        void returnNull_whenInvalidUserIdIsProvided() {
            String id = "yh45g";

            Point point = pointService.findPointByUserId(id);

            assertThat(point).isNull();
        }
    }

    @DisplayName("포인트 충전 통합 테스트")
    @Nested
    class Charge {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwsChargeAmountFailException_whenUserIDIsNotProvided() {
            String id = "yh45g";

            CoreException exception = assertThrows(CoreException.class, () -> pointService.chargePoint(id, 1000L));

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
