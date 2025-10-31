package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto.SignUpRequest;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import com.loopers.utils.DatabaseCleanUp;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest {

  private static final String ENDPOINT_POST = "/api/v1/users";
  private static final Function<String, String> ENDPOINT_GET = userId -> "/api/v1/users/" + userId;

  private final TestRestTemplate testRestTemplate;
  private final UserRepository userRepository;
  private final DatabaseCleanUp databaseCleanUp;

  @Autowired
  public UserV1ApiE2ETest(
      TestRestTemplate testRestTemplate,
      UserRepository userRepository,
      DatabaseCleanUp databaseCleanUp
  ) {
    this.testRestTemplate = testRestTemplate;
    this.userRepository = userRepository;
    this.databaseCleanUp = databaseCleanUp;
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("POST /api/v1/users")
  @Nested
  class Post {

    @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
    @Test
    void returnsCreatedUserInfo_whenValidRequestIsProvided() {
      // arrange
      UserV1Dto.SignUpRequest request = new SignUpRequest(
          "validId10", "valid@email.com", "2025-10-28", Gender.FEMALE);

      // act
      ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<UserResponse>> response =
          testRestTemplate.exchange(
              ENDPOINT_POST, HttpMethod.POST, new HttpEntity<>(request), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
          () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),

          () -> assertThat(response.getBody().data().userId()).isEqualTo(request.userId()),
          () -> assertThat(response.getBody().data().email()).isEqualTo(request.email())
      );
    }

    @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
    @Test
    void returnsBadRequest_whenGenderIsNull() {
      // arrange
      UserV1Dto.SignUpRequest request = new SignUpRequest(
          "validId10", "valid@email.com", "2025-10-28", null);

      // act
      ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<UserResponse>> response =
          testRestTemplate.exchange(
              ENDPOINT_POST, HttpMethod.POST, new HttpEntity<>(request), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is4xxClientError()),
          () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
      );
    }
  }

  @DisplayName("Get /api/v1/users/{userId}")
  @Nested
  class Get {

    @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
    @Test
    void returnsUserResponse_whenValidIdIsProvided() {
      // arrange
      User user = userRepository.save(
          new User("validId10", "valid@email.com", "2025-10-28", Gender.FEMALE)
      );
      String requestUrl = ENDPOINT_GET.apply(user.getUserId());

      // act
      ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<UserResponse>> response =
          testRestTemplate.exchange(
              requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is2xxSuccessful()),

          () -> assertThat(response.getBody().data().userId()).isEqualTo(user.getUserId()),
          () -> assertThat(response.getBody().data().email()).isEqualTo(user.getEmail())
      );
    }

    @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
    @Test
    void returnsNotFound_whenInvalidIdIsProvided() {
      // arrange
      String invalidUserId = "non-existent-user-id";
      String requestUrl = ENDPOINT_GET.apply(invalidUserId);

      // act
      ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<UserResponse>> response =
          testRestTemplate.exchange(
              requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is4xxClientError()),
          () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
      );
    }
  }
}
