package com.loopers.domain.productlike

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.Email
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeCountJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.KafkaMockConfig
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@Import(KafkaMockConfig::class)
@RecordApplicationEvents
@SpringBootTest
class ProductLikeServiceIntegrationTest @Autowired constructor(
    private val productLikeService: ProductLikeService,
    private val databaseCleanUp: DatabaseCleanUp,
    private val productJpaRepository: ProductJpaRepository,
    private val productLikeJpaRepository: ProductLikeJpaRepository,
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val redisCleanUp: RedisCleanUp,
) {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

    @AfterEach
    fun tearDown() {
        redisCleanUp.truncateAll()
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
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
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
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
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
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
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
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
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

    /*
     **🔗 통합 테스트
    - [ ] 동일한 상품에 대해 여러명이 좋아요 등록을 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.
    - [ ] 동일한 상품에 대해 여러명이 좋아요 취소를 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.
    - [ ] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 등록을 요청해도, 상품의 좋아요는 1번만 등록되어야 한다.
    - [ ] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 취소를 요청해도, 상품의 좋아요는 1번만 취소되어야 한다.
     */
    @DisplayName("좋아요 등록/취소 동시성 테스트, ")
    @Nested
    inner class Concurrency {

        @DisplayName("[비관적 락] 동일한 상품에 대해 여러명이 좋아요 등록을 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Test
        fun multipleUsersLikeSameProduct() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            val userIds = mutableListOf<Long>()
            repeat(numberOfThreads) {
                val createdUser = userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                userIds.add(createdUser.id)
            }

            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, 0))

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.like(ProductLikeCommand.Like(userIds[it], createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertThat(productLikeCount).isNotNull
            assertThat(productLikeCount?.productLikeCount).isEqualTo(10L)
        }

        @DisplayName("[비관적 락] 동일한 상품에 대해 여러명이 좋아요 취소를 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Test
        fun multipleUsersUnikeSameProduct() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            val userIds = mutableListOf<Long>()
            repeat(numberOfThreads) {
                val createdUser = userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                productLikeJpaRepository.save(ProductLikeEntity(createdUser.id, createdProduct.id))
                userIds.add(createdUser.id)
            }

            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, userIds.size))

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.unlike(ProductLikeCommand.Unlike(userIds[it], createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertThat(productLikeCount).isNotNull
            assertThat(productLikeCount?.productLikeCount).isZero()
        }

        @DisplayName("[비관적 락] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 등록을 요청해도, 상품의 좋아요는 1번만 등록되어야 한다.")
        @Test
        fun singleUserLikesSameProductMultipleTimes() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.like(ProductLikeCommand.Like(createdUser.id, createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertThat(productLikeCount?.productLikeCount).isEqualTo(1)
        }

        @DisplayName("[비관적 락] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 취소를 요청해도, 상품의 좋아요는 1번만 취소되어야 한다.")
        @Test
        fun singleUserUnlikesSameProductMultipleTimes() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            productLikeJpaRepository.save(ProductLikeEntity(createdUser.id, createdProduct.id))
            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, 1))

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.unlike(ProductLikeCommand.Unlike(createdUser.id, createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeService.getProductLikeCount(createdProduct.id)
            assertThat(productLikeCount?.productLikeCount).isZero()
        }

        @DisplayName("[낙관적 락] 동일한 상품에 대해 여러명이 좋아요 등록을 동시에 요청할 때 좋아요 집계 이벤트[ProductLikeEvent]는 정상적으로 발행되어야 한다.")
        @Test
        fun multipleUsersLikeSameProductWithOptimisticLock() {
            // given
            val numberOfThreads = 20
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            val userIds = mutableListOf<Long>()

            repeat(numberOfThreads) {
                val createdUser = userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                userIds.add(createdUser.id)
            }

            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, 0))

            // when
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.likeOptimistic(ProductLikeCommand.Like(userIds[it], createdProduct.id))
                    } catch (e: OptimisticLockingFailureException) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // then
            assertThat(applicationEvents.stream(ProductLikedEvent::class.java).count()).isEqualTo(numberOfThreads.toLong())
        }

        @DisplayName("[낙관적 락] 동일한 상품에 대해 여러명이 좋아요 취소를 요청할 때 좋아요 집계 이벤트[ProductUnlikeEvent]는 정상적으로 발행되어야 한다.")
        @Test
        fun multipleUsersUnikeSameProductWithOptimisticLock() {
            // arrange
            val numberOfThreads = 20
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            val userIds = mutableListOf<Long>()

            repeat(numberOfThreads) {
                val createdUser = userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                productLikeJpaRepository.save(ProductLikeEntity(createdUser.id, createdProduct.id))
                userIds.add(createdUser.id)
            }

            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, userIds.size))

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.unlikeOptimistic(ProductLikeCommand.Unlike(userIds[it], createdProduct.id))
                    } catch (e: OptimisticLockingFailureException) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            assertThat(applicationEvents.stream(ProductUnlikedEvent::class.java).count()).isEqualTo(numberOfThreads.toLong())
        }

        @DisplayName("[낙관적 락] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 등록을 요청해도 좋아요 집계 이벤트[ProductLikeEvent]는 정상적으로 발행되어야 한다.")
        @Test
        fun singleUserLikesSameProductMultipleTimesWithOptimisticLock() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.likeOptimistic(ProductLikeCommand.Like(createdUser.id, createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            assertThat(applicationEvents.stream(ProductLikedEvent::class.java).count()).isEqualTo(1L)
        }

        @DisplayName("[낙관적 락] 동일한 상품에 대해 한명이 동시에 여러 번 좋아요 취소를 요청해도 좋아요 집계 이벤트[ProductUnlikeEvent]는 정상적으로 발행되어야 한다.")
        @Test
        fun singleUserUnlikesSameProductMultipleTimesWithOptimisticLock() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            productLikeJpaRepository.save(ProductLikeEntity(createdUser.id, createdProduct.id))
            productLikeCountJpaRepository.save(ProductLikeCountEntity(createdProduct.id, 1))

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeService.unlikeOptimistic(ProductLikeCommand.Unlike(createdUser.id, createdProduct.id))
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            assertThat(applicationEvents.stream(ProductUnlikedEvent::class.java).count()).isEqualTo(1L)
        }
    }
}
