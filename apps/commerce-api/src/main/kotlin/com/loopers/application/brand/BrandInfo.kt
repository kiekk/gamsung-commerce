package com.loopers.application.brand

import com.loopers.domain.brand.BrandEntity
import com.loopers.support.enums.brand.BrandStatusType

class BrandInfo {
    data class BrandResult(
        val id: Long,
        val name: String,
        val status: BrandStatusType,
    ) {
        companion object {
            fun from(brand: BrandEntity): BrandResult {
                return BrandResult(
                    brand.id,
                    brand.name,
                    brand.status,
                )
            }
        }
    }
}
