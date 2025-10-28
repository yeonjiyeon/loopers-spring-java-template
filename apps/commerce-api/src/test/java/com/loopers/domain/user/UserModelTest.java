package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserModelTest {
    @DisplayName("회원 가입을 할 때, ")
    @Nested
    class Create {
        // ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다. - 영문 및 숫자가 아닌 경우")
        @Test
        void throwsException_whenIdIsInvalidFormat_NotAlphanumeric() {
            // arrange
            String invalidId = "user!@#";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(invalidId);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("ID는 영문 및 숫자 10자 이내여야 합니다.");
        }

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다. - 영문 및 숫자 10자 초과인 경우")
        @Test
        void throwsException_whenIdIsInvalidFormat_TooLong() {
            // arrange
            String invalidId = "user1234567";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(invalidId);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("ID는 영문 및 숫자 10자 이내여야 합니다.");
        }

        // extra case
        // 0자 이하인 경우
        // 숫자만 있는 경우
    }
}
