package com.loopers.application.brand

import com.loopers.domain.brand.BrandEntity
import com.loopers.support.enums.brand.BrandStatusType

class BrandInfo {
    data class BrandResponse(
        val id: Long,
        val name: String,
        val status: BrandStatusType,
    ) {
        companion object {
            fun from(brand: BrandEntity): BrandResponse {
                return BrandResponse(
                    brand.id,
                    brand.name,
                    brand.status,
                )
            }
        }
    }
}
