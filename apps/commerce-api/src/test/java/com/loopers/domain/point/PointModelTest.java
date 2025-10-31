package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointModelTest {
    @DisplayName("포인트 충전을 할 때, ")
    @Nested
    class Create {
        // 0 이하의 정수로 포인트를 충전 시 실패한다.
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, -10, -100})
        void throwsException_whenPointIsZeroOrNegative(int invalidPoint) {
            // arrange
            String validId = "user123";
            String validEmail = "xx@yy.zz";
            String validBirthday = "1993-03-13";
            String validGender = "male";

            User user = User.create(validId, validEmail, validBirthday, validGender);

            // act
            CoreException result = assertThrows(CoreException.class, () -> Point.create(user, invalidPoint));

            // assert
            assertThat(result.getMessage()).isEqualTo("충전 포인트는 0보다 커야 합니다.");
        }
    }
}
