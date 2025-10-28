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
        private final String validId = "user123";
        private final String validEmail = "xx@yy.zz";

        // ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다. - 영문 및 숫자가 아닌 경우")
        @Test
        void throwsException_whenIdIsInvalidFormat_NotAlphanumeric() {
            // arrange
            String invalidId = "user!@#";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(invalidId, validEmail);
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
                User.create(invalidId, validEmail);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("ID는 영문 및 숫자 10자 이내여야 합니다.");
        }

        // extra case
        // 0자 이하인 경우
        // 숫자만 있는 경우

        // 이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.
        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다. - @가 없는 경우")
        @Test
        void throwsException_whenEmailIsInvalidFormat_MissingAtSymbol() {
            // arrange
            String invalidEmail = "userexample.com";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(validId, invalidEmail);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다. - 도메인 부분이 없는 경우")
        @Test
        void throwsException_whenEmailIsInvalidFormat_MissingDomain() {
            // arrange
            String invalidEmail = "user@.com";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(validId, invalidEmail);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다. - 최상위 도메인이 없는 경우")
        @Test
        void throwsException_whenEmailIsInvalidFormat_MissingTopLevelDomain() {
            // arrange
            String invalidEmail = "user@example";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(validId, invalidEmail);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다. - @.만 있는 경우")
        @Test
        void throwsException_whenEmailIsInvalidFormat_OnlyAtAndDot() {
            // arrange
            String invalidEmail = "@.";

            // act
            CoreException result = assertThrows(CoreException.class, () -> {
                User.create(validId, invalidEmail);
            });

            // assert
            assertThat(result.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        }

        // extra case
        // 공백이 포함된 경우
    }
}
