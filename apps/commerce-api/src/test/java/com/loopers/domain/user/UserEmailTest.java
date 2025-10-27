package com.loopers.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserEmail Test")
class UserEmailTest {

    @DisplayName("UserEmail 모델을 생성할 때")
    @Nested
    class Create {
        @DisplayName("UserEmail이 형식에 맞으면, 정상적으로 생성된다.")
        @Test
        void createUserEmail_whenValid() {

            // given
            String email = "yh45g@gmail.com";

            // when
            UserEmail userEmail = new UserEmail(email);

            // then
            Assertions.assertThat(userEmail).extracting("email").isEqualTo(email);

        }
    }
}
