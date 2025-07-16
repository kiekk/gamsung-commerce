package com.loopers.domain.user

class UserEntityFixture {

    private var userId: String = "userId123"
    private var email: String = "shyoon991@gmail.com"
    private var birthday: String = "2000-01-01"
    private var gender: UserEntity.GenderType = UserEntity.GenderType.M

    companion object {
        fun aUser(): UserEntityFixture = UserEntityFixture()
    }

    fun userId(userId: String): UserEntityFixture = apply { this.userId = userId }

    fun email(email: String): UserEntityFixture = apply { this.email = email }

    fun birthday(birthday: String): UserEntityFixture = apply { this.birthday = birthday }

    fun gender(gender: UserEntity.GenderType): UserEntityFixture = apply { this.gender = gender }

    fun build(): UserEntity = UserEntity(
        userId = userId,
        email = email,
        birthday = birthday,
        gender = gender,
    )
}
