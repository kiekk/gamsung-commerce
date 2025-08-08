package com.loopers.domain.brand

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.support.enums.brand.BrandStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BrandEntityTest {

    /*
     **🧱 단위 테스트**
    - [ ]  브랜드명이 `한글, 영문, 숫자 20자 이내` 형식에 맞지 않으면, BrandEntity 생성에 실패한다.
    - [ ]  브랜드명이 올바른 경우 BrandEntity를 생성한다.
     */
    @DisplayName("브랜드를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("브랜드명이 `한글, 영문, 숫자 20자 이내` 형식에 맞지 않으면, BrandEntity 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(
            strings = [
                "", // 빈 문자열인 경우
                "브랜드@이름", // 특수문자가 포함된 경우
                "브랜드 이름", // 공백이 포함된 경우
                "브랜드이름!", // 특수문자가 포함된 경우
                "브랜드이름1234567890abcdef", // 길이가 21인 경우
            ],
        )
        fun failsToCreateBrand_whenNameIsInvalid(invalidBrandName: String) {
            // arrange

            // act
            val result = assertThrows<IllegalArgumentException> {
                BrandEntity(
                    invalidBrandName,
                )
            }

            // assert
            assertAll(
                { assertThat(result).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(result.message).isEqualTo("브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.") },
            )
        }

        @DisplayName("브랜드명이 올바른 경우 BrandEntity를 생성한다.")
        @Test
        fun createsBrand_whenNameAndStatusAreValid() {
            // arrange
            val validBrandName = "브랜드이름"

            // act
            val brandEntity = BrandEntity(
                validBrandName,
            )

            // assert
            assertThat(brandEntity.name).isEqualTo(validBrandName)
        }
    }

    /*
     **🧱 단위 테스트**
    - [ ]  브랜드를 비활성화 할 경우 브랜드 상태는 INACTIVE가 된다.
    - [ ]  브랜드가 퇴점한 경우 브랜드 상태는 CLOSED가 된다.
     */
    @DisplayName("브랜드 상태를 변경할 때, ")
    @Nested
    inner class ChangeStatus {
        @DisplayName("브랜드를 비활성화 할 경우 브랜드 상태는 INACTIVE가 된다.")
        @Test
        fun deactivatesBrand() {
            // arrange
            val brandEntity = aBrand().name("브랜드이름").build()

            // act
            brandEntity.inactive()

            // assert
            assertThat(brandEntity.status).isEqualTo(BrandStatusType.INACTIVE)
        }

        @DisplayName("브랜드가 퇴점한 경우 브랜드 상태는 WITHDRAWN가 된다.")
        @Test
        fun withdrawsBrand() {
            // arrange
            val brandEntity = aBrand().name("브랜드이름").build()

            // act
            brandEntity.close()

            // assert
            assertThat(brandEntity.status).isEqualTo(BrandStatusType.CLOSED)
        }
    }
}
