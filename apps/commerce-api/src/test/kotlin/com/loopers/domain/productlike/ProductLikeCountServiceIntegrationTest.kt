package com.loopers.domain.productlike

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.productlike.fixture.ProductLikeCountEntityFixture.Companion.aProductLikeCount
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeCountJpaRepository
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ProductLikeCountServiceIntegrationTest @Autowired constructor(
    private val productLikeCountService: ProductLikeCountService,
    private val productJpaRepository: ProductJpaRepository,
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] [낙관적 락] 동일한 상품에 대해 여러명이 좋아요 등록을 동시에 요청할 때, 상품의 좋아요 개수가 정상 반영되어야 한다.
     */
    @DisplayName("상품 좋아요 수 등록 요청을 할 때, ")
    @Nested
    inner class Increase {
        @DisplayName("[낙관적 락] 동일한 상품에 대해 좋아요 등록을 동시에 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Test
        fun multipleIncreaseWithOptimisticLock() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct.id).productLikeCount(0).build())

            var successCount = 0
            var failureCount = 0

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeCountService.increase(createdProduct.id)
                        successCount++
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                        failureCount++
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeCountJpaRepository.findById(createdProduct.id).get()
            assertThat(productLikeCount).isNotNull
            assertThat(productLikeCount.productLikeCount).isEqualTo(numberOfThreads - failureCount)
        }
    }


    /*
    **🔗 통합 테스트
    - [ ] [낙관적 락] 동일한 상품에 대해 여러명이 좋아요 취소를 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.
     */
    @DisplayName("상품 좋아요 수 취소 요청을 할 때, ")
    @Nested
    inner class Decrease {
        @DisplayName("[낙관적 락] 동일한 상품에 대해 좋아요 취소를 동시에 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Test
        fun multipleDecreaseWithOptimisticLock() {
            // arrange
            val numberOfThreads = 10
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().build())
            productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct.id).productLikeCount(10).build())

            var successCount = 0
            var failureCount = 0

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        productLikeCountService.decrease(createdProduct.id)
                        successCount++
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                        failureCount++
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val productLikeCount = productLikeCountJpaRepository.findById(createdProduct.id).get()
            assertThat(productLikeCount).isNotNull
            assertThat(productLikeCount.productLikeCount).isEqualTo(numberOfThreads - successCount)
        }
    }
}
