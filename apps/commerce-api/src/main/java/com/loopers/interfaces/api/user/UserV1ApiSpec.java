package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "사용자 관련 API 입니다.")
public interface UserV1ApiSpec {

  @Operation(
      summary = "회원 가입",
      description = "신규 사용자를 등록합니다."
  )
  ApiResponse<UserResponse> signUp(UserV1Dto.SignUpRequest request);

  @Operation(
      summary = "내 정보 조회",
      description = "헤더의 ID로 내 정보를 조회합니다."
  )
  ApiResponse<UserV1Dto.UserResponse> getMyInfo(
      @Schema(name = "USER-ID", description = "조회할 사용자 ID (헤더)")
          String userId
  );

}
