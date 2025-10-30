package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.user.User.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @DisplayName("User 객체 생성 테스트")
  @Nested
  class Create {

    private final String VALID_USER_ID = "validId10";
    private final String VALID_EMAIL = "valid@email.com";
    private final String VALID_BIRTHDATE = "2025-10-28";
    private final Gender VALID_GENDER = Gender.FEMALE;

    @DisplayName("모든 값이 유효하면 User 객체 생성에 성공한다.")
    @Test
    void create_user_with_valid_data() {
      // assert
      assertDoesNotThrow(() -> {
        new User(VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
      });
    }

    @DisplayName("USER_ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.(BAD_REQUEST 예외 발생)")
    @Nested
    class UserIdValidation {

      @Test
      @DisplayName("글자수가 10자를 넘어가는 케이스")
      void throwsBadRequest_whenUserIdIsOver10Chars() {
        // arrange
        assertUserIdCreationFails("aaaaaaaaaaa");
      }

      @Test
      @DisplayName("영문/숫자 외 문자가 들어가는 경우")
      void throwsBadRequest_whenUserIdContainsNonAlphanumeric() {
        // arrange
        assertUserIdCreationFails("유저");
      }

      @Test
      @DisplayName("빈 문자열인 경우")
      void throwsBadRequest_whenUserIdIsEmpty() {
        // arrange
        assertUserIdCreationFails("");
      }

      private void assertUserIdCreationFails(String invalidUserId) {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new User(invalidUserId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
    }


    @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @Nested
    class EmailValidation {

      @DisplayName("Email이 '@'를 포함하지 않으면 생성에 실패한다.")
      @Test
      void when_email_lacks_at_sign_then_fail() {
        // arrange
        assertEmailCreationFails("invalid.email.com");
      }

      @DisplayName("Email이 빈 문자열이면 생성에 실패한다.")
      @Test
      void when_email_is_empty_then_fail() {
        // arrange
        assertEmailCreationFails("");
      }

      @DisplayName("Email 형식이 유효하지 않으면 생성에 실패한다.")
      @Test
      void when_email_is_invalid_legacy_case_then_fail() {
        // arrange
        assertEmailCreationFails("aaaaaaaaaaa");
      }

      private void assertEmailCreationFails(String invalidEmail) {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new User(VALID_USER_ID, invalidEmail, VALID_BIRTHDATE, VALID_GENDER);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }
    }

    @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @Nested
    class BirthdateValidation {

      private void assertBirthdateCreationFails(String invalidBirthdate) {
        // act
        CoreException result = assertThrows(CoreException.class, () -> {
          new User(VALID_USER_ID, VALID_EMAIL, invalidBirthdate, VALID_GENDER);
        });

        // assert
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
      }

      @Test
      @DisplayName("생년월일의 입력 형식이 다르면 생성에 실패한다.")
      void throwsBadRequest_whenBirthdateHasNoHyphens() {
        assertBirthdateCreationFails("20251028");
      }

      @Test
      @DisplayName("생년월일이 존재하지 않는 날짜(2월 30일)이면 생성에 실패한다.")
      void throwsBadRequest_whenBirthdateIsInvalidDay() {
        assertBirthdateCreationFails("2025-02-30");
      }

      @Test
      @DisplayName("생년월일이 빈 문자열이면 생성에 실패한다.")
      void throwsBadRequest_whenBirthdateIsEmpty() {
        assertBirthdateCreationFails("");
      }
    }

  }

}
