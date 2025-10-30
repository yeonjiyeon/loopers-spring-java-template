package com.loopers.domain.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @DisplayName("회원 가입시 User 저장이 수행된다.( spy 검증 )")
    @Test
    void savesUser_whenSignUpIsSuccessful() {

      UserCommand.UserCreationCommand creationCommand = new UserCommand.UserCreationCommand(
          "validId10", "valid@email.com", "2025-10-28");

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
      User existingUser = new User(duplicateId, "original@email.com", "2000-01-01");

      // act
      userRepository.save(existingUser);

      UserCommand.UserCreationCommand duplicateCommand = new UserCommand.UserCreationCommand(
          duplicateId, "new@email.com", "2001-01-01");


      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.signUp(duplicateCommand);
      });


      // assert
      assertEquals("이미 가입된 ID입니다.", exception.getMessage());


    }
  }

}
