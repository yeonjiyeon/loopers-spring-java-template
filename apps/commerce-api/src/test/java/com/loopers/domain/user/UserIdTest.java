package com.loopers.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserId Test")
class UserIdTest {

    @DisplayName("UserId 모델을 생성할 때")
    @Nested
    class Create {
        @DisplayName("userId가 형식에 맞으면, 정상적으로 생성된다.")
        @Test
        void createUserId_whenValid() {

            // given
            String id = "yh45g";

            // when
            UserId userId = new UserId(id);

            // then
            Assertions.assertThat(userId).extracting("id").isEqualTo(id);

        }
    }
}
