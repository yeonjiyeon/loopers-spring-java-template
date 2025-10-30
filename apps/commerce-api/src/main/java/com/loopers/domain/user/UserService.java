package com.loopers.domain.user;

import com.loopers.domain.user.UserCommand.UserCreationCommand;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserService {

  private final UserRepository userRepository;

  public UserResponse signUp(UserCreationCommand command) {
    userRepository.findByUserId(command.userId()).ifPresent(user -> {
      throw new IllegalArgumentException("이미 가입된 ID입니다.");
    });

    userRepository.findByUserId(command.userId());
    User user = userRepository.save(command.toUser());
    return UserResponse.from(user);
  }

  public User getUser(String userId) {
    return userRepository.findByUserId(userId).orElse(null);
  }
}
