package com.loopers.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    val zipCode: String,
    val address: String,
    val addressDetail: String?,
) {
    init {
        !zipCode.matches(ZIP_CODE_PATTERN) && throw IllegalArgumentException("우편번호는 5자리 숫자여야 합니다.")
        !address.matches(ADDRESS_PATTERN) && throw IllegalArgumentException("주소 형식이 올바르지 않습니다.")
        addressDetail?.let {
            !it.matches(ADDRESS_DETAIL_PATTERN) && throw IllegalArgumentException("상세주소 형식이 올바르지 않습니다.")
        }
    }

    companion object {
        private val ZIP_CODE_PATTERN = Regex("^\\d{5}$")
        private val ADDRESS_PATTERN = Regex("^[가-힣a-zA-Z0-9\\s#.,\\-]{1,100}$")
        private val ADDRESS_DETAIL_PATTERN = Regex("^[가-힣a-zA-Z0-9\\s#.,\\-]{1,100}$")
    }
}
