package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PointTest {
    @DisplayName("Point 단위 테스트")
    @Nested
    class Charge {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @Test
        void throwsChargeAmountFailException_whenZeroAmountOrNegative() {
            //given
            Point point = new Point("yh45g", 0L);

            //when&then
            assertThrows(CoreException.class, () ->
                    point.charge(0L));
        }
    }

}
