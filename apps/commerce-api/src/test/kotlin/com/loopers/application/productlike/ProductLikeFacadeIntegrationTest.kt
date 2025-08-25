package com.loopers.application.productlike

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.productlike.ProductLikeCommand
import com.loopers.domain.productlike.ProductLikeService
import com.loopers.domain.productlike.fixture.ProductLikeEntityFixture.Companion.aProductLike
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.user.UserService
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Price
import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.enums.user.GenderType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents

@RecordApplicationEvents
@SpringBootTest
class ProductLikeFacadeIntegrationTest @Autowired constructor(
    private val productLikeFacade: ProductLikeFacade,
    private val userService: UserService,
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val productLikeJpaRepository: ProductLikeJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val redisCleanUp: RedisCleanUp,
) {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
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
            )
            val createdProduct = productService.createProduct(productCreateCommand)
            val nonExistentUsername = "nonexistentuser"
            val likeCommand = ProductLikeCriteria.Like(nonExistentUsername, createdProduct.id)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.like(likeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("존재하지 않는 상품에 대해 좋아요 등록 요청을 하면 404 Not Found 에러가 발생한다.")
        @Test
        fun failsToLikeProduct_whenProductNotFound() {
            // arrange
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val nonExistentProductId = 999L
            val likeCommand = ProductLikeCriteria.Like(createdUser.username, nonExistentProductId)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.like(likeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                {
                    assertThat(exception.message).contains("상품을 찾을 수 없습니다. productId: $nonExistentProductId")
                },
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
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val createdProduct = productService.createProduct(productCreateCommand)
            val likeCommand = ProductLikeCriteria.Like(createdUser.username, createdProduct.id)

            // act
            productLikeFacade.like(likeCommand)

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val callProductLikeEventCount = applicationEvents.stream(ProductLikeEvent::class.java)
                .filter { event -> event.productId == createdProduct.id }.count()
            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.id) },
                { assertThat(productLikes[0].productId).isEqualTo(createdProduct.id) },
                { assertThat(callProductLikeEventCount).isEqualTo(1) },
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
        @Test
        fun failsToUnlikeProduct_whenUserNotFound() {
            // arrange
            val command = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
            )
            val createdProduct = productService.createProduct(command)
            val nonExistentUsername = "nonexistentuser"
            val unlikeCommand = ProductLikeCriteria.Unlike(nonExistentUsername, createdProduct.id)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.unlike(unlikeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("존재하지 않는 상품에 대해 좋아요 취소 요청을 하면 404 Not Found 에러가 발생한다.")
        @Test
        fun failsToUnlikeProduct_whenProductNotFound() {
            // arrange
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
            )
            val createdUser = userService.save(userSignUpCommand)
            val nonExistentProductId = 999L
            val unlikeCommand = ProductLikeCriteria.Unlike(createdUser.username, nonExistentProductId)

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.unlike(unlikeCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                {
                    assertThat(exception.message).contains("상품을 찾을 수 없습니다. productId: $nonExistentProductId")
                },
            )
        }

        @DisplayName("상품 좋아요 취소에 성공하면 상품 좋아요 이력이 삭제된다.")
        @Test
        fun unlikesProductSuccessfully() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
            )
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
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
            productLikeFacade.unlike(ProductLikeCriteria.Unlike(createdUser.username, createdProduct.id))

            // assert
            val productLikes = productLikeService.getProductLikesByUserId(createdUser.id)
            val callProductUnlikeEventCount = applicationEvents.stream(ProductUnlikeEvent::class.java)
                .filter { event -> event.productId == createdProduct.id }.count()
            assertAll(
                { assertThat(productLikes).isEmpty() },
                { assertThat(callProductUnlikeEventCount).isEqualTo(1) },
            )
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 사용자가 좋아요한 상품 목록을 조회할 때, 존재하지 않는 사용자의 경우 404 Not Found 에러가 발생한다.
    - [ ] 사용자가 좋아요한 상품 목록을 조회할 때, 존재하는 사용자의 경우 해당 사용자가 좋아요한 상품 목록이 반환된다.
    - [ ] 사용자가 좋아요한 상품 목록을 조회할 때, 해당 사용자가 좋아요한 상품이 없는 경우 빈 목록이 반환된다.
     */
    @DisplayName("사용자가 좋아요한 상품 목록을 조회할 때, ")
    @Nested
    inner class GetUserLikeProducts {
        @DisplayName("존재하지 않는 사용자의 경우 404 Not Found 에러가 발생한다.")
        @Test
        fun failsToGetUserLikeProducts_whenUserNotFound() {
            // arrange
            val nonExistentUsername = "nonexistentuser"

            // act
            val exception = assertThrows<CoreException> {
                productLikeFacade.getUserLikeProducts(nonExistentUsername)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("존재하는 사용자의 경우 해당 사용자가 좋아요한 상품 목록이 반환된다.")
        @Test
        fun returnsUserLikeProducts_whenUserExists() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            repeat(10) {
                val createdProduct = productJpaRepository.save(aProduct().name("상품$it").build())
                productLikeJpaRepository.save(aProductLike().userId(createdUser.id).productId(createdProduct.id).build())
            }

            // act
            val userLikeProducts = productLikeFacade.getUserLikeProducts(createdUser.username)

            // assert
            assertAll(
                { assertThat(userLikeProducts).isNotEmpty },
                { assertThat(userLikeProducts.size).isEqualTo(10) },
            )
        }

        @DisplayName("해당 사용자가 좋아요한 상품이 없는 경우 빈 목록이 반환된다.")
        @Test
        fun returnsEmptyList_whenUserHasNoLikedProducts() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())

            // act
            val userLikeProducts = productLikeFacade.getUserLikeProducts(createdUser.username)

            // assert
            assertAll(
                { assertThat(userLikeProducts).isEmpty() },
                { assertThat(userLikeProducts.size).isEqualTo(0) },
            )
        }
    }
}
