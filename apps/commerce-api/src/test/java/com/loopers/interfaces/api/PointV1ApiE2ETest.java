package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.user.User;
import com.loopers.domain.user.User.Gender;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.interfaces.api.point.PointV1Dto.ChargePointsRequest;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest {

  private static final String ENDPOINT_GET = "/api/v1/point";
  private static final String ENDPOINT_POST = "/api/v1/point";

  private final TestRestTemplate testRestTemplate;
  private final UserRepository userRepository;
  private final DatabaseCleanUp databaseCleanUp;

  @Autowired
  public PointV1ApiE2ETest(
      TestRestTemplate testRestTemplate,
      UserRepository userRepository,
      DatabaseCleanUp databaseCleanUp
  ) {
    this.testRestTemplate = testRestTemplate;
    this.userRepository = userRepository;
    this.databaseCleanUp = databaseCleanUp;
  }

  @AfterEach
  void tearDown() {
    databaseCleanUp.truncateAllTables();
  }

  @DisplayName("GET /api/v1/point")
  @Nested
  class Get {

    @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
    @Test
    void returnsPoint_whenHeaderIsProvided() {
      // arrange
      int expectedPoint = 10;

      User user = userRepository.save(
          new User("validId10", "valid@email.com", "2025-10-28", Gender.FEMALE, expectedPoint)
      );

      HttpHeaders headers = new HttpHeaders();
      headers.set("X-USER-ID", user.getUserId());
      HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);

      // act
      ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {
      };
      ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
          testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, httpEntity, responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
          () -> assertEquals(expectedPoint, response.getBody().data().point())
      );
    }



    @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
    @Test
    void returnsBadRequest_whenHeaderIsMissing() {
      // arrange
      HttpEntity<String> httpEntity = new HttpEntity<>(null, new HttpHeaders());

      // act
      ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
      };
      ResponseEntity<ApiResponse<Object>> response =
          testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, httpEntity, responseType);

      assertAll(
          () -> assertTrue(response.getStatusCode().is4xxClientError()),
          () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode())
      );
    }
  }

  @DisplayName("Post /api/v1/point")
  @Nested
  class Post {

    @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
    @Test
    void returnsPoint_whenHeaderIsProvided() {
      // arrange
      int expectedPoint = 1000;

      User user = userRepository.save(
          new User("validId10", "valid@email.com", "2025-10-28", Gender.FEMALE)
      );

      ChargePointsRequest request = new ChargePointsRequest(user.getUserId(), expectedPoint);

      // act
      ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
          testRestTemplate.exchange(ENDPOINT_POST, HttpMethod.POST, new HttpEntity<>(request), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
          () -> assertThat(response.getBody().data().point()).isEqualTo(expectedPoint)
      );
    }



    @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
    @Test
    void returnsPoint_whenHeaderddIsProvided() {
      // arrange
      String invalidUserId = "non-existent-user-id";
      int expectedPoint = 1000;

      ChargePointsRequest request = new ChargePointsRequest(invalidUserId, expectedPoint);

      // act
      ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
          testRestTemplate.exchange(ENDPOINT_POST, HttpMethod.POST, new HttpEntity<>(request), responseType);

      // assert
      assertAll(
          () -> assertTrue(response.getStatusCode().is4xxClientError()),
          () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
      );
    }
  }


}
