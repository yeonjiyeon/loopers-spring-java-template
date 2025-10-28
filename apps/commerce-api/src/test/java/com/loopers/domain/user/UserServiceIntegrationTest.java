package com.loopers.domain.user;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("통합 테스트 ")
    @Nested
    class UserRegister {

        @DisplayName("회원 가입시 User 저장이 수행된다.")
        @Test
        void save_whenUserRegister() {
            String userId = "yh45g";
            String email = "yh45g@loopers.com";
            String brith = "1994-12-05";

            User user = new User(userId, email, brith);
            UserRepository userRepositorySpy = spy(userRepository);
            UserService userServiceSpy = new UserService(userRepositorySpy);
            userServiceSpy.register(userId, email, brith);

            verify(userRepositorySpy).save(user);
        }
    }
}
