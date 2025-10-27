package com.loopers.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserBirth Test")
class UserBirthTest {
    @DisplayName("UserBirth 모델을 생성할 때")
    @Nested
    class Create {
        @DisplayName("UserBirth이 형식에 맞으면, 정상적으로 생성된다.")
        @Test
        void createUserBirth_whenValid() {
            String birth = "1994-12-01";

            UserBirth userBirth = new UserBirth(birth);

            assertThat(userBirth).extracting("birth").isEqualTo(birth);
        }
    }
}
