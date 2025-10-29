package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(String userId, String email, String birth, String gender) {
        userRepository.findByUserId(userId).ifPresent(user -> {
            throw new CoreException(ErrorType.CONFLICT);
        });

        User user = new User(userId, email, birth, gender);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUserId(String userId){
        return userRepository.findByUserId(userId);
    }

}
