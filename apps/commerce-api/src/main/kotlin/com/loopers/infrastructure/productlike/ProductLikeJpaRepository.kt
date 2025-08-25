package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProductLikeJpaRepository : JpaRepository<ProductLikeEntity, Long> {
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserId(userId: Long): List<ProductLikeEntity>

    @Modifying
    @Query(
        value = """
        DELETE FROM product_like
        WHERE product_id = :productId
        AND user_id = :userId
        """,
        nativeQuery = true,
    )
    fun deleteByUserIdAndProductId(userId: Long, productId: Long): Int
}
