package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    User register(String userId, String email, String birth) {
        userRepository.findByUserId(userId).ifPresent(user -> {
            throw new CoreException(ErrorType.CONFLICT);
        });

        User user = new User(userId, email, birth);
        return userRepository.save(user);
    }

}
