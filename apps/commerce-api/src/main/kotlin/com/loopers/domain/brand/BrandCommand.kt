package com.loopers.domain.brand

import com.loopers.domain.brand.BrandEntity.BrandStatusType

class BrandCommand {
    data class Create(
        val name: String,
        val status: BrandStatusType,
    ) {
        fun toEntity(): BrandEntity {
            return BrandEntity(
                name,
                status,
            )
        }
    }
}
