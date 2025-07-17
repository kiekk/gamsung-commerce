package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "User V1 API", description = "사용자 API 입니다.")
interface UserV1ApiSpec {

    @Operation(
        summary = "회원 가입",
        description = "요청한 정보로 회원 가입을 진행합니다.",
    )
    fun signUp(
        @Schema(name = "회원 가입 정보", description = "회원 가입할 사용자의 요청 정보")
        request: UserV1Dto.SignUpRequest,
    ): ApiResponse<UserV1Dto.UserResponse>

    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다.",
    )
    fun getMyInfo(
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<UserV1Dto.UserResponse>
}
