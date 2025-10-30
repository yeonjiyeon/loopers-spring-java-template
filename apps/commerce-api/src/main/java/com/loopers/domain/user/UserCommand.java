package com.loopers.domain.user;

public class UserCommand {

  public record UserCreationCommand(
      String userId,
      String email,
      String birthDate
  ) {

    public User toUser() {
      return new User(this.userId, this.email, this.birthDate);
    }
  }
}
