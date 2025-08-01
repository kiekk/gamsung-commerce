package com.loopers.domain.product

import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ProductEntityTest {
    /*
     * 🧱 단위 테스트
    - [ ] 상품명이 `한글, 영문, 숫자 20자 이내` 형식에 맞지 않으면, ProductEntity 객체 생성에 실패한다.
    - [ ] 상품 설명이 `100자 이내` 형식에 맞지 않으면, ProductEntity 객체 생성에 실패한다.
    - [ ] 상품명, 설명, 가격, 재고, 상품 상태가 유효한 경우, ProductEntity 객체를 생성한다.
     */
    @DisplayName("상품 엔티티를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("상품명이 `한글, 영문, 숫자 20자 이내` 형식에 맞지 않으면, ProductEntity 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(
            strings = [
                "", // 빈 문자열인 경우
                "Invalid Name!", // 특수문자가 포함된 경우
                "상품이름1234567890abcdefg", // 길이가 21인 경우
            ],
        )
        fun failsToCreateProductEntity_whenNameIsInvalid(invalidProductName: String) {
            // arrange

            // act
            val exception = assertThrows<IllegalArgumentException> {
                ProductEntity(
                    1L,
                    invalidProductName,
                    "Valid description.",
                    Price(1000L),
                    ProductStatusType.ACTIVE,
                )
            }

            // assert
            assertAll(
                { assertEquals(IllegalArgumentException::class.java, exception::class.java) },
                { assertEquals("상품명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.", exception.message) },
            )
        }

        @DisplayName("상품 설명이 `100자 이내` 형식에 맞지 않으면, ProductEntity 객체 생성에 실패한다.")
        @Test
        fun failsToCreateProductEntity_whenDescriptionIsTooLong() {
            // arrange
            val longDescription = "a".repeat(101) // 101자 이상의 설명

            // act
            val exception = assertThrows<IllegalArgumentException> {
                ProductEntity(
                    1L,
                    "상품A",
                    longDescription,
                    Price(1000L),
                    ProductStatusType.ACTIVE,
                )
            }

            // assert
            assertAll(
                { assertEquals(IllegalArgumentException::class.java, exception::class.java) },
                { assertEquals("상품 설명은 최대 100자 이내로 입력해야 합니다.", exception.message) },
            )
        }

        @DisplayName("상품명, 설명, 가격, 재고, 상품 상태가 유효한 경우, ProductEntity 객체를 생성한다.")
        @Test
        fun createsProductEntity_whenNameAndDescriptionAreValid() {
            // arrange
            val productName = "ValidProductName"
            val description = "This is a valid product description."
            val price = 1000L
            val status = ProductStatusType.ACTIVE

            // act
            val productEntity = ProductEntity(
                1L,
                productName,
                description,
                Price(1000L),
                ProductStatusType.ACTIVE,
            )

            // assert
            assertAll(
                { assertEquals(productName, productEntity.name) },
                { assertEquals(description, productEntity.description) },
                { assertEquals(price, productEntity.price.value) },
                { assertEquals(status, productEntity.status) },
            )
        }
    }
}
