package com.loopers.domain.productlike

import com.loopers.application.product.ProductLikeCriteria
import com.loopers.application.product.ProductLikeFacade
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserService
import com.loopers.domain.vo.Price
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductLikeFacadeIntegrationTest @Autowired constructor(
    private val productLikeFacade: ProductLikeFacade,
    private val userService: UserService,
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 존재하지 않는 사용자가 상품 좋아요 등록 요청을 하면 404 Not Found 에러가 발생한다.
    - [ ] 존재하지 않는 상품에 대해 좋아요 등록 요청을 하면 404 Not Found 에러가 발생한다.
    - [ ] 상품 좋아요 등록에 성공하면 상품 좋아요 수가 증가하고 상품 상품 좋아요 이력이 추가된다.
     */
    @DisplayName("상품 좋아요 등록 요청을 할 때, ")
    @Nested
    inner class Like {
        @DisplayName("존재하지 않는 사용자가 상품 좋아요 등록 요청을 하면 404 Not Found 에러가 발생한다.")
        @Test
        fun failsToLikeProduct_whenUserNotFound() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct = productService.createProduct(productCreateCommand)
            val nonExistentUserId = 999L
            val likeCommand = ProductLikeCriteria.Like(nonExistentUserId, createdProduct.id)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.like(likeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. userId: ${nonExistentUserId}") },
            )
        }

        @DisplayName("존재하지 않는 상품에 대해 좋아요 등록 요청을 하면 404 Not Found 에러가 발생한다.")
        @Test
        fun failsToLikeProduct_whenProductNotFound() {
            // arrange
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val nonExistentProductId = 999L
            val likeCommand = ProductLikeCriteria.Like(createdUser.id, nonExistentProductId)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.like(likeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("상품을 찾을 수 없습니다. productId: ${nonExistentProductId}") },
            )
        }

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
            val likeCommand = ProductLikeCriteria.Like(createdUser.id, createdProduct.id)

            // act
            productLikeFacade.like(likeCommand)

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
    - [ ] 존재하지 않는 사용자가 상품 좋아요 취소 요청을 하면 404 Not Found 에러가 발생한다.
    - [ ] 존재하지 않는 상품에 대해 좋아요 취소 요청을 하면 404 Not Found 에러가 발생한다.
    - [ ] 상품 좋아요 취소에 성공하면 상품 좋아요 수가 감소하고 상품 좋아요 이력이 삭제된다.
     */
    @DisplayName("상품 좋아요 취소 요청을 할 때, ")
    @Nested
    inner class Unlike {
        @DisplayName("존재하지 않는 사용자가 상품 좋아요 취소 요청을 하면 404 Not Found 에러가 발생한다.")
        fun failsToUnlikeProduct_whenUserNotFound() {
            // arrange
            val command = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct = productService.createProduct(command)
            val nonExistentUserId = 999L
            val unlikeCommand = ProductLikeCriteria.Unlike(nonExistentUserId, createdProduct.id)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.unlike(unlikeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. userId: ${nonExistentUserId}") },
            )
        }

        @DisplayName("존재하지 않는 상품에 대해 좋아요 취소 요청을 하면 404 Not Found 에러가 발생한다.")
        fun failsToUnlikeProduct_whenProductNotFound() {
            // arrange
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                "shyoon991@gmail.com",
                "2000-01-01",
                UserEntity.GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val nonExistentProductId = 999L
            val unlikeCommand = ProductLikeCriteria.Unlike(createdUser.id, nonExistentProductId)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.unlike(unlikeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("상품을 찾을 수 없습니다. productId: ${nonExistentProductId}") },
            )
        }

        @DisplayName("상품 좋아요 취소에 성공하면 상품 좋아요 이력이 삭제된다.")
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
            productLikeFacade.unlike(ProductLikeCriteria.Unlike(createdUser.id, createdProduct.id))

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertAll(
                { assertThat(productLikes).isEmpty() },
                { assertThat(productLikeCount?.productLikeCount).isEqualTo(0) },
            )
        }
    }
}
