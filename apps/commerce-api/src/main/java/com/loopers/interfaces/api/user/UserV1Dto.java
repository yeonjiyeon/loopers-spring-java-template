package com.loopers.interfaces.api.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand.UserCreationCommand;
import jakarta.validation.constraints.NotNull;

public class UserV1Dto {

  public record SignUpRequest(
      @NotNull String userId,
      @NotNull String email,
      @NotNull String birthDate
  ) {
    public UserCreationCommand toCommand() {
      return new UserCreationCommand(userId, email, birthDate);
    }
  }

  public record UserResponse(String userId, String email, String birthDate) {

    public static UserV1Dto.UserResponse from(User user) {
      return new UserV1Dto.UserResponse(
          user.getUserId(),
          user.getEmail(),
          user.getBirthdate()
      );
    }
  }


}
