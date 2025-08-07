package com.loopers.application.user

import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService,
) {
    fun getMyInfo(userId: String): UserInfo.UserDetail {
        return userService.findUserBy(userId)
            ?.let { UserInfo.UserDetail.from(it) }
            ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "사용자를 찾을 수 없습니다: $userId",
            )
    }

    fun signUp(criteria: UserCriteria.SignUp): UserInfo.UserResult {
        return userService.save(criteria.toCommand())
            .let { UserInfo.UserResult.from(it) }
    }
}
