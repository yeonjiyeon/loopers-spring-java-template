package com.loopers.interfaces.api.user;

import com.loopers.domain.user.User;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ControllerTest {

    private static final String REGISTER_ENDPOINT = "/api/v1/users/register";
    private static final Function<String, String> GETUSER_ENDPOINT = id -> "/api/v1/users/" + id;
    private final TestRestTemplate testRestTemplate;
    private final UserJpaRepository userJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ControllerTest(TestRestTemplate testRestTemplate, UserJpaRepository userJpaRepository, DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.userJpaRepository = userJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class RegisterUser {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void registerUser_whenSuccessResponseUser() {
            String userId = "yh45g";
            String email = "yh45g@loopers.com";
            String birth = "1994-12-05";
            String gender = "MALE";

            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest(userId, email, birth, gender);

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(REGISTER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(request), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(userId),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(email),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo(birth),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(gender)
            );
        }
        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 BAD_REQUEST 응답을 받는다.")
        @Test
        void throwsBadRequest_whenGenderIsNotProvided() {
            String userId = "yh45g";
            String email = "yh45g@loopers.com";
            String birth = "1994-12-05";
            String gender =  null;

            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest(userId, email, birth, gender);

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(REGISTER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(request), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("GET /api/v1/users/{userId}")
    @Nested
    class GetUserById {
        @DisplayName("해당 ID의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void getUserById_whenSuccessResponseUser() {
            String userId = "yh45g";
            String email = "yh45g@loopers.com";
            String birth = "1994-12-05";
            String gender = "MALE";

            userJpaRepository.save(new User(userId, email, birth, gender));

            String requestUrl = GETUSER_ENDPOINT.apply(userId);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(userId),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(email),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo(birth),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(gender)
            );
        }

        @DisplayName("해당 ID의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void throwsException_whenInvalidUserIdIsProvided() {
            String userId = "notUserId";
            String requestUrl = GETUSER_ENDPOINT.apply(userId);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            HttpHeaders headers = new HttpHeaders();
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

}
