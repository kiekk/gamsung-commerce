package com.loopers.domain.user

import com.loopers.domain.BaseEntity

class UserEntity(
    val userId: String,
    val email: String,
    val birthday: String,
    val genderType: GenderType,
) : BaseEntity() {

    enum class GenderType {
        M,
        F,
    }
}
