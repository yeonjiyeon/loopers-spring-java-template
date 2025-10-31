package com.loopers.domain.user;

import com.loopers.domain.user.User.Gender;

public class UserCommand {

  public record UserCreationCommand(
      String userId,
      String email,
      String birthDate,
      Gender gender) {

    public User toUser() {
      return new User(this.userId, this.email, this.birthDate, this.gender);
    }
  }
}
