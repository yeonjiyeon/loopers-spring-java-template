package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  private final String VALID_USER_ID = "validId10";
  private final String VALID_EMAIL = "valid@email.com";
  private final String VALID_BIRTHDATE = "2025-10-28";
  private final Gender VALID_GENDER = Gender.FEMALE;

  @DisplayName("User 객체 생성 테스트")
  @Nested
  class Create {

    @DisplayName("모든 값이 유효하면 User 객체 생성에 성공한다.")
    @Test
    void create_user_with_valid_data() {
      // assert
      assertDoesNotThrow(() -> {
        createUser(VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
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
          createUser(invalidUserId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
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
          createUser(VALID_USER_ID, invalidEmail, VALID_BIRTHDATE, VALID_GENDER);
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
          createUser(VALID_USER_ID, VALID_EMAIL, invalidBirthdate, VALID_GENDER);
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

  @DisplayName("포인트 충전 테스트")
  @Nested
  class ChargePoint {

    @DisplayName("0으로 포인트를 충전 시 실패한다.")
    @Test
    void throwsBadRequest_whenChargeAmountIsZero() {
      assertChargePointFails(0);
    }

    @DisplayName("음수 금액으로 충전 시 실패한다 (BAD_REQUEST 예외 발생).")
    @Test
    void throwsBadRequest_whenChargeAmountIsNegative() {
      assertChargePointFails(-100);
    }

    @DisplayName("양수 금액으로 충전 시 성공한다.")
    @Test
    void chargePoint_with_positive_amount() {
      // arrange
      User user = createUser(VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
      int chargeAmount = 100;
      Point expectedPoint = new Point(100);

      // act
      Point chargePoint = user.getPoint().add(chargeAmount);

      // assert
      assertEquals(expectedPoint, chargePoint);
    }

    private void assertChargePointFails(int invalidAmount) {
      // arrange
      User user = createUser(VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
      Point initialPoint = user.getPoint();

      // act
      CoreException exception = assertThrows(CoreException.class, () -> {
        user.getPoint().add(invalidAmount);
      });

      // assert
      assertEquals(ErrorType.BAD_REQUEST, exception.getErrorType());
      assertEquals(initialPoint, user.getPoint());
    }
  }

  private User createUser(String userId, String email, String birthdate,
      Gender gender) {
    return new User(userId, email, birthdate, gender);
  }

}
