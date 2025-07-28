package com.loopers.domain.product

import com.loopers.domain.product.ProductLikeCountEntityFixture.Companion.aProductLikeCount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class ProductLikeCountEntityTest {

    /*
    **🧱 단위 테스트**
    - [ ] 상품 좋아요 수가 음수일 경우, ProductLikeCountEntity를 생성한다.
    - [ ] 상품 ID와 상품 좋아요 수가 올바른 경우, ProductLikeCountEntity를 생성한다.
     */
    @DisplayName("상품 좋아요 수를 생성할 떄, ")
    @Nested
    inner class Create {

        @DisplayName("상품 좋아요 수가 음수일 경우, ProductLikeCountEntity를 생성한다.")
        @Test
        fun failsToCreateProductLikeCount_whenLikeCountIsNegative() {
            // arrange
            val invalidProductLikeCount = -1

            // act & assert
            val exception = assertThrows<IllegalArgumentException> {
                aProductLikeCount().productLikeCount(invalidProductLikeCount).build()
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("좋아요 수는 0 이상이어야 합니다.") },
            )

        }

        @DisplayName("상품 ID와 상품 좋아요 수가 올바른 경우, ProductLikeCountEntity를 생성한다.")
        @Test
        fun createsProductLikeCount_whenProductIdAndLikeCountAreValid() {
            // arrange
            val validProductLikeCount = 0

            // act
            val productLikeCountEntity = aProductLikeCount().productLikeCount(validProductLikeCount).build()

            // assert
            assertAll(
                { assertThat(productLikeCountEntity.productLikeCount).isEqualTo(validProductLikeCount) },
            )
        }
    }

    /*
    **🧱 단위 테스트**
    - [ ] 상품 좋아요 수를 증가시킬 수 있다.
    - [ ] 상품 좋아요 수를 감소시킬 수 있다.
    - [ ] 상품 좋아요 수가 0인 경우 상품 좋아요 수를 감소해도 0 이하로 내려가지 않는다.
     */
    @DisplayName("상품 좋아요 수를 변경할 때, ")
    @Nested
    inner class Update {
        @DisplayName("상품 좋아요 수를 증가시킬 수 있다.")
        @Test
        fun increasesLikeCount() {
            // arrange
            val initialProductLikeCount = 0
            val productLikeCountEntity = aProductLikeCount().productLikeCount(initialProductLikeCount).build()

            // act
            productLikeCountEntity.increaseProductLikeCount()

            // assert
            assertThat(productLikeCountEntity.productLikeCount).isEqualTo(initialProductLikeCount + 1)
        }

        @DisplayName("상품 좋아요 수를 감소시킬 수 있다.")
        @Test
        fun decreasesLikeCount() {
            // arrange
            val initialProductLikeCount = 1
            val productLikeCountEntity = aProductLikeCount().productLikeCount(initialProductLikeCount).build()

            // act
            productLikeCountEntity.decreaseProductLikeCount()

            // assert
            assertThat(productLikeCountEntity.productLikeCount).isEqualTo(0)
        }

        @DisplayName("상품 좋아요 수가 0인 경우 상품 좋아요 수를 감소해도 0 이하로 내려가지 않는다.")
        @Test
        fun doesNotDecreaseLikeCountBelowZero() {
            // arrange
            val initialProductLikeCount = 0
            val productLikeCountEntity = aProductLikeCount().productLikeCount(initialProductLikeCount).build()

            // act
            productLikeCountEntity.decreaseProductLikeCount()

            // assert
            assertThat(productLikeCountEntity.productLikeCount).isEqualTo(0)
        }
    }
}
