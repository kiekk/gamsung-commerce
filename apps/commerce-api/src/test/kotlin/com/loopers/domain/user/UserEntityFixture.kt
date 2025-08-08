package com.loopers.domain.user

import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.support.enums.user.GenderType

class UserEntityFixture {

    private var username: String = "userId123"
    private var name: String = "soono"
    private var email: Email = Email("shyoon991@gmail.com")
    private var birthday: Birthday = Birthday("2000-01-01")
    private var gender: GenderType = GenderType.M

    companion object {
        fun aUser(): UserEntityFixture = UserEntityFixture()
    }

    fun username(username: String): UserEntityFixture = apply { this.username = username }

    fun name(name: String): UserEntityFixture = apply { this.name = name }

    fun email(email: Email): UserEntityFixture = apply { this.email = email }

    fun birthday(birthday: Birthday): UserEntityFixture = apply { this.birthday = birthday }

    fun gender(gender: GenderType): UserEntityFixture = apply { this.gender = gender }

    fun build(): UserEntity = UserEntity(
        username,
        name,
        email,
        birthday,
        gender,
    )
}
