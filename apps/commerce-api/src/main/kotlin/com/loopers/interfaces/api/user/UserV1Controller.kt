package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(
    private val userService: UserService,
    private val userFacade: UserFacade,
) : UserV1ApiSpec {

    @PostMapping
    override fun signUp(@RequestBody request: UserV1Dto.SignUpRequest): ApiResponse<UserV1Dto.UserResponse> {
        return userService.save(request.toEntity())
            .let { UserV1Dto.UserResponse.fromEntity(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("/me")
    override fun getMyInfo(userId: String): ApiResponse<UserV1Dto.UserResponse> {
        return userFacade.getUserInfo(userId)
            .let { UserV1Dto.UserResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

}
