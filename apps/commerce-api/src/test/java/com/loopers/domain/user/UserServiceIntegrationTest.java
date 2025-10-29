package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserJpaRepository spyUserRepository;

    @DisplayName("회원 가입시 User 저장이 수행된다.")
    @Test
    void saveUserWhenRegister() {
        // arrange
        String validId = "user123";
        String validEmail = "xx@yy.zz";
        String validBirthday = "1993-03-13";

        // act
        // 유저 등록
        userService.registerUser(validId, validEmail, validBirthday);
        // 저장된 유저 조회
        Optional<User> foundUser = userService.findByUserId(validId);

        // assert
        verify(spyUserRepository).save(any(User.class));
        verify(spyUserRepository).findByUserId("user123");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo("user123");
    }

    @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
    @Test
    void throwsExceptionWhenRegisterWithExistingUserId() {
        // arrange
        String validId = "user123";
        String validEmail = "xx@yy.zz";
        String validBirthday = "1993-03-13";
        String validOtherEmail = "zz@cc.xx";
        String validOtherBirthday = "1992-06-07";

        // act
        // 기존 유저 등록
        userService.registerUser(validId, validEmail, validBirthday);
        // 동일 ID 로 유저 등록 시도
        CoreException result = assertThrows(CoreException.class, () -> {
            userService.registerUser(validId, validOtherEmail, validOtherBirthday);
        });

        // assert
        assertThat(result.getMessage()).isEqualTo("이미 존재하는 사용자 ID 입니다.");
    }

}
