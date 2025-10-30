package com.loopers.application.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo registerUser(String userId, String email, String birthday, String gender) {
        User registeredUser = userService.registerUser(userId, email, birthday, gender);
        return UserInfo.from(registeredUser);
    }
}
