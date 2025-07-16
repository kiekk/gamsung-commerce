package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

@Entity
@Table(name = "member")
class UserEntity(
    @Column(name = "userId", unique = true, nullable = false)
    val userId: String,
    @Column(name = "email", unique = true, nullable = false)
    val email: String,
    @Column(name = "birthday", nullable = false)
    val birthday: String,
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    val gender: GenderType,
) : BaseEntity() {

    enum class GenderType {
        M,
        F,
    }

    init {
        if (userId.length > 10 || !userId.matches(Regex("^[a-zA-Z0-9]{1,10}$"))) {
            throw CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내여야 합니다.")
        }
        if (!email.matches(Regex("^(?!.*\\.\\.)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            throw CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.")
        }
        if (!birthday.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
            throw CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (yyyy-MM-dd)")
        }
        try {
            val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
            LocalDate.parse(birthday, formatter)
        } catch (e: DateTimeParseException) {
            throw CoreException(ErrorType.BAD_REQUEST, "존재하지 않는 생년월일입니다. (${birthday})")
        }

    }
}
