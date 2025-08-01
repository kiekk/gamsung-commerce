package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.support.enums.user.GenderType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class UserEntity(
    @Column(name = "username", unique = true, nullable = false)
    val username: String,
    val name: String,
    @Column(name = "email", unique = true, nullable = false)
    val email: Email,
    val birthday: Birthday,
    @Enumerated(EnumType.STRING)
    val gender: GenderType,
) : BaseEntity() {

    init {
        (username.length > 10 || !username.matches(USERNAME_PATTERN)) &&
                throw IllegalArgumentException("ID는 영문 및 숫자 10자 이내여야 합니다.")
    }

    companion object {
        private val USERNAME_PATTERN = "^[a-zA-Z0-9]{1,10}$".toRegex()
    }
}
