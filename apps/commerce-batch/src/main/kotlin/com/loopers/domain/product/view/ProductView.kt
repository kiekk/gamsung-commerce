package com.loopers.domain.product.view

import com.loopers.support.enums.product.ProductStatusType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "product")
class ProductView(
    @Id
    val id: Long,
    val brandId: Long,
    val name: String,
    val description: String?,
    val price: Long,
    @Enumerated(EnumType.STRING)
    val status: ProductStatusType,
    val createdAt: LocalDateTime,
)
