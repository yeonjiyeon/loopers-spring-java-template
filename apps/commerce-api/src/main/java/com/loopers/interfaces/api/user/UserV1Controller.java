package com.loopers.interfaces.api.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

  private final UserService userService;

  @PostMapping("")
  @Override
  public ApiResponse<UserResponse> signUp(@RequestBody UserV1Dto.SignUpRequest request) {

    UserResponse response = userService.signUp(request.toCommand());
    return ApiResponse.success(response);
  }

  @GetMapping("/{userId}")
  @Override
  public ApiResponse<UserResponse> getMyInfo(@PathVariable(value = "userId") String userId) {
    User user = userService.getUser(userId);

    if(user == null) {
      throw new CoreException(ErrorType.NOT_FOUND, "[userId = " + userId + "] 예시를 찾을 수 없습니다.");
    }
    UserResponse response = UserResponse.from(user);
    return ApiResponse.success(response);
  }

}
