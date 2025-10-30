package com.loopers.domain.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.user.User.Gender;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class UserServiceIntegrationTest {

  @Autowired
  UserService userService;

  @MockitoSpyBean
  UserRepository userRepository;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("회원 가입을 할 때,")
  @Nested
  class UserCreationCommand {

    private final String VALID_USER_ID = "validId10";
    private final String VALID_EMAIL = "valid@email.com";
    private final String VALID_BIRTHDATE = "2025-10-28";
    private final Gender VALID_GENDER = Gender.FEMALE;

    @DisplayName("회원 가입시 User 저장이 수행된다.( spy 검증 )")
    @Test
    void savesUser_whenSignUpIsSuccessful() {

      UserCommand.UserCreationCommand creationCommand = new UserCommand.UserCreationCommand(
          VALID_USER_ID, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);

      //act
      UserResponse result = userService.signUp(creationCommand);

      // assert
      assertAll(
          () -> verify(userRepository, times(1)).save(any(User.class)),
          () -> assertNotNull(result),
          () -> assertEquals(creationCommand.userId(), result.userId()),
          () -> assertEquals(creationCommand.email(), result.email()),
          () -> assertEquals(creationCommand.birthDate(), result.birthDate())
      );
    }

    @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다")
    @Test
    void throwsException_whenInvalidIdIsProvided() {
      // arrange
      String duplicateId = "dupliId";
      User existingUser = new User(duplicateId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);

      // act
      userRepository.save(existingUser);

      UserCommand.UserCreationCommand duplicateCommand = new UserCommand.UserCreationCommand(
          duplicateId, "new@email.com", "2001-01-01", Gender.MALE);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.signUp(duplicateCommand);
      });

      // assert
      assertEquals("이미 가입된 ID입니다.", exception.getMessage());
    }


    @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다")
    @Test
    void return_userInfo_whenValidIdIsProvided() {
      // arrange
      String findId = "findId";
      User existingUser = new User(findId, VALID_EMAIL, VALID_BIRTHDATE, VALID_GENDER);

      // act
      userRepository.save(existingUser);

      User result = userService.getUser(findId);

      // assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertEquals(findId, result.getUserId()),
          () -> assertEquals(VALID_EMAIL, result.getEmail()),
          () -> assertEquals(VALID_BIRTHDATE, result.getBirthdate())
      );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void returnsNull_whenUserNotFound() {
      // arrange
      String nonExistingId = "nonExistingId";

      // act
      User result = userService.getUser(nonExistingId);

      // assert
      assertNull(result);
    }

  }
}
