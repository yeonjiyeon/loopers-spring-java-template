package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class PointServiceIntegrationTest {

  @Autowired
  PointService pointService;

  @MockitoSpyBean
  UserRepository userRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  private final String VALID_USER_ID = "validId10";
  private final String VALID_EMAIL = "valid@email.com";
  private final String VALID_BIRTHDATE = "2025-10-28";
  private final Gender VALID_GENDER = Gender.FEMALE;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }


  @DisplayName("포인트 조회 할 때,")
  @Nested
  class Get {

    @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
    @Test
    void return_user_point_whenValidIdIsProvided() {
      // arrange
      String findId = "findId";
      Point expectedPoint = new Point(10);
      User existingUser = new User(findId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER, expectedPoint);

      // act
      userRepository.save(existingUser);

      Point point = pointService.getPoint(findId);

      // assert
      assertAll(
          () -> assertNotNull(point),
          () -> assertEquals(expectedPoint.getAmount(), point.getAmount())
      );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void return_null_whenInvalidIdIsProvided() {
      // arrange
      String invalidId = "non-existent-user-id";

      // act
      Point point = pointService.getPoint(invalidId);

      // assert
      assertNull(point);
    }
  }

  @DisplayName("포인트 충전을 할 때,")
  @Nested
  class chargePoint {

    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void charge_not_point_whenInvalidIdIsProvided() {

      String invalidId = "non-existent-user-id";
      int chargeAmount = 100;

      //act
      CoreException exception = assertThrows(CoreException.class, () -> {
        pointService.charge(invalidId, chargeAmount);
      });

      // assert
      assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }
  }

}
