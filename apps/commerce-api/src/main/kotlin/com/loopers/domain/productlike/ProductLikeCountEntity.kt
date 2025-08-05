package com.loopers.domain.productlike

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "product_like_count")
class ProductLikeCountEntity(
    @Column(unique = true)
    val productId: Long,
    var productLikeCount: Int = 0,
) : BaseEntity() {
    @Version
    var version: Long? = null

    init {
        require(productLikeCount >= 0) { "좋아요 수는 0 이상이어야 합니다." }
    }

    fun increaseProductLikeCount() {
        productLikeCount++
    }

    fun decreaseProductLikeCount() {
        require(productLikeCount > 0) { "좋아요 수는 0보다 작을 수 없습니다." }
        productLikeCount--
    }
}
