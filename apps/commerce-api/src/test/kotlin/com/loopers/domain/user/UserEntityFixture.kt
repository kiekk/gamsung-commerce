package com.loopers.domain.user

import com.loopers.support.enums.user.GenderType

class UserEntityFixture {

    private var userId: String = "userId123"
    private var name: String = "soono"
    private var email: String = "shyoon991@gmail.com"
    private var birthday: String = "2000-01-01"
    private var gender: GenderType = GenderType.M

    companion object {
        fun aUser(): UserEntityFixture = UserEntityFixture()
    }

    fun userId(userId: String): UserEntityFixture = apply { this.userId = userId }

    fun name(name: String): UserEntityFixture = apply { this.name = name }

    fun email(email: String): UserEntityFixture = apply { this.email = email }

    fun birthday(birthday: String): UserEntityFixture = apply { this.birthday = birthday }

    fun gender(gender: GenderType): UserEntityFixture = apply { this.gender = gender }

    fun build(): UserEntity = UserEntity(
        userId,
        name,
        email,
        birthday,
        gender,
    )
}
