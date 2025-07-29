package com.loopers.domain.productlike

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserService
import com.loopers.domain.vo.Price
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductLikeServiceIntegrationTest @Autowired constructor(
    private val productLikeService: ProductLikeService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var userService: UserService

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 상품 좋아요 등록에 성공하면 상품 좋아요 수가 증가하고 상품 좋아요 이력이 추가된다.
    - [ ] 상품 좋아요 등록 시, 이미 좋아요를 누른 상품에 대해서는 중복 등록이 되지 않는다.
     */
    @DisplayName("상품 좋아요 등록 요청을 할 때, ")
    @Nested
    inner class Like {
        @DisplayName("상품 좋아요 등록에 성공하면 상품 좋아요 수가 증가하고 상품 좋아요 이력이 추가된다.")
        @Test
        fun likesProductSuccessfully() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val createdProduct = productService.createProduct(productCreateCommand)
            val productLikeCommand = ProductLikeCommand.Like(
                createdUser.id,
                createdProduct.id,
            )

            // act
            productLikeService.like(productLikeCommand)

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.id) },
                { assertThat(productLikes[0].productId).isEqualTo(createdProduct.id) },
                { assertThat(productLikeCount?.productLikeCount).isEqualTo(1) },
            )
        }

        @DisplayName("상품 좋아요 등록 시, 이미 좋아요를 누른 상품에 대해서는 중복 등록이 되지 않는다.")
        @Test
        fun doesNotAllowDuplicateLikes() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val createdProduct = productService.createProduct(productCreateCommand)
            val productLikeCommand = ProductLikeCommand.Like(
                createdUser.id,
                createdProduct.id,
            )

            // act
            productLikeService.like(productLikeCommand)
            productLikeService.like(productLikeCommand)
            productLikeService.like(productLikeCommand)

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.id) },
                { assertThat(productLikes[0].productId).isEqualTo(createdProduct.id) },
                { assertThat(productLikeCount?.productLikeCount).isEqualTo(1) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 상품 좋아요 취소에 성공하면 상품 좋아요 수가 감소하고 상품 좋아요 이력이 삭제된다.
    - [ ] 상품 좋아요 취소 시, 이미 좋아요를 취소한 상품에 대해서는 중복 취소가 되지 않는다.
     */
    @DisplayName("상품 좋아요 취소 요청을 할 때, ")
    @Nested
    inner class Unlike {
        @DisplayName("상품 좋아요 취소에 성공하면 상품 좋아요 수가 감소하고 상품 좋아요 이력이 삭제된다.")
        @Test
        fun unlikesProductSuccessfully() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val createdProduct = productService.createProduct(productCreateCommand)
            productLikeService.like(
                ProductLikeCommand.Like(
                    createdUser.id,
                    createdProduct.id,
                ),
            )

            // act
            productLikeService.unlike(
                ProductLikeCommand.Unlike(
                    createdUser.id,
                    createdProduct.id,
                ),
            )

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertAll(
                { assertThat(productLikes).isEmpty() },
                { assertThat(productLikeCount?.productLikeCount).isZero() },
            )
        }

        @DisplayName("상품 좋아요 취소 시, 이미 좋아요를 취소한 상품에 대해서는 중복 취소가 되지 않는다.")
        @Test
        fun doesNotAllowDuplicateUnlikes() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val createdProduct = productService.createProduct(productCreateCommand)
            productLikeService.like(
                ProductLikeCommand.Like(
                    createdUser.id,
                    createdProduct.id,
                ),
            )

            // act
            val productUnlikeCommand = ProductLikeCommand.Unlike(
                createdUser.id,
                createdProduct.id,
            )
            productLikeService.unlike(productUnlikeCommand)
            productLikeService.unlike(productUnlikeCommand)
            productLikeService.unlike(productUnlikeCommand)

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertAll(
                { assertThat(productLikes).isEmpty() },
                { assertThat(productLikeCount?.productLikeCount).isZero() },
            )
        }
    }
}
