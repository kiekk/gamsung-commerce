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

    data class Deduct(
        val productId: Long,
        val quantity: Int,
    ) {
        companion object {
            fun from(productQuantityMap: Map<Long, Int>): List<Deduct> {
                return productQuantityMap.map { (productId, quantity) ->
                    Deduct(productId, quantity)
                }
            }
        }
    }
}
