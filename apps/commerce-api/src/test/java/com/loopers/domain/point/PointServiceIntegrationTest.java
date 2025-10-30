package com.loopers.domain.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
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
      int expectedPoint = 10;
      User existingUser = new User(findId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER, expectedPoint);

      // act
      userRepository.save(existingUser);

      Integer point = pointService.getPoint(findId);

      // assert
      assertAll(
          () -> assertNotNull(point),
          () -> assertEquals(expectedPoint, point.intValue())
      );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void return_null_whenInvalidIdIsProvided() {
      // arrange
      String invalidId = "non-existent-user-id";

      // act
      Integer point = pointService.getPoint(invalidId);

      // assert
      assertNull(point);
    }
  }

//  @DisplayName("회원 가입을 할 때,")
//  @Nested
//  class Create {
//
//    @DisplayName("회원 가입시 User 저장이 수행된다.( spy 검증 )")
//    @Test
//    void savesUser_whenSignUpIsSuccessful() {
//
//      UserCommand.UserCreationCommand creationCommand = new UserCommand.UserCreationCommand(
//          VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
//
//      //act
//      UserResponse result = userService.signUp(creationCommand);
//
//      // assert
//      assertAll(
//          () -> verify(userRepository, times(1)).save(any(User.class)),
//          () -> assertNotNull(result),
//          () -> assertEquals(creationCommand.userId(), result.userId()),
//          () -> assertEquals(creationCommand.email(), result.email()),
//          () -> assertEquals(creationCommand.birthDate(), result.birthDate())
//      );
//    }
//
//    @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다")
//    @Test
//    void throwsException_whenInvalidIdIsProvided() {
//      // arrange
//      String duplicateId = "dupliId";
//      User existingUser = new User(duplicateId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);
//
//      // act
//      userRepository.save(existingUser);
//
//      UserCommand.UserCreationCommand duplicateCommand = new UserCommand.UserCreationCommand(
//          duplicateId, "new@email.com", "2001-01-01", Gender.MALE);
//
//      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//        userService.signUp(duplicateCommand);
//      });
//
//      // assert
//      assertEquals("이미 가입된 ID입니다.", exception.getMessage());
//    }
//  }

}
