package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @DisplayName("User 객체 생성 테스트")
  @Nested
  class Create {

    @DisplayName("ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.(BAD_REQUEST 예외 발생)")
    @Test
    void when_id_is_invalid_then_fail_to_create_user() {
      // arrange
      String id = "aaaaaaaaaaa";

      // act
      CoreException result = assertThrows(CoreException.class, () -> {
        new User("user1", id, "2025-10-28");
      });

      // assert
      assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @Test
    void when_email_is_invalid_then_fail_to_create_user() {
      // arrange
      String email = "aaaaaaaaaaa";

      // act
      CoreException result = assertThrows(CoreException.class, () -> {
        new User("user1", email, "2025-10-28");
      });

      // assert
      assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @Test
    void when_birthdate_is_invalid_then_fail_to_create_user() {
      // arrange
      String birthdate = "aaaaaaaaaaa";

      // act
      CoreException result = assertThrows(CoreException.class, () -> {
        new User("user1", "a@mail.com", birthdate);
      });

      // assert
      assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

  }

}
