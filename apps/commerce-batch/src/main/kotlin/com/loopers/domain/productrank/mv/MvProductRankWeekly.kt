package com.loopers.domain.productrank.mv

import com.loopers.support.enums.product.ProductStatusType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "mv_product_rank_weekly")
class MvProductRankWeekly(
    @Id
    val productId: Long,
    val aggregateDate: LocalDate,
    val rankNumber: Long,
    val productName: String,
    val productPrice: Long,
    @Enumerated(EnumType.STRING)
    val productStatus: ProductStatusType,
    val brandName: String,
    val productLikeCount: Int,
    val createdAt: LocalDateTime,
)
