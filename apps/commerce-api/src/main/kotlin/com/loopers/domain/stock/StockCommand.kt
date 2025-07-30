package com.loopers.domain.stock

class StockCommand {
    data class Create(
        val productId: Long,
        val quantity: Int,
    ) {
        fun toEntity(): StockEntity {
            return StockEntity(
                productId,
                quantity,
            )
        }
    }
}
