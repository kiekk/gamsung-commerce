package com.loopers.application.user

import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService,
) {
    fun getUserInfo(userId: String): UserInfo {
        // user가 있으면 반환, 없으면 CoreException 발생
        return userService.getUser(userId)
            ?.let { UserInfo.from(it) }
            ?: throw CoreException(
                errorType = ErrorType.NOT_FOUND,
                customMessage = "사용자를 찾을 수 없습니다: $userId",
            )
    }

    fun signUp(signUp: UserInfo.SignUp): UserInfo {
        return userService.save(signUp.toUserEntity())
            .let { UserInfo.from(it) }
    }
}
