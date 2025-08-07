package com.loopers.interfaces.api.brand

import com.loopers.application.brand.BrandCriteria
import com.loopers.application.brand.BrandInfo
import com.loopers.support.enums.brand.BrandStatusType
import jakarta.validation.constraints.NotBlank

class BrandV1Dto {
    data class QueryRequest(
        val name: String? = null,
        val status: BrandStatusType? = null,
    ) {
        fun toCriteria(): BrandCriteria.Query {
            return BrandCriteria.Query(
                name,
                status,
            )
        }
    }

    data class CreateRequest(
        @field:NotBlank(message = "브랜드명은 필수 입력값입니다.")
        val name: String,
    ) {
        fun toCriteria(username: String): BrandCriteria.Create {
            return BrandCriteria.Create(
                username,
                name,
            )
        }
    }

    data class BrandResponse(
        val id: Long,
        val name: String,
        val status: BrandStatusType,
    ) {
        companion object {
            fun from(brand: BrandInfo.BrandResult): BrandResponse {
                return BrandResponse(
                    brand.id,
                    brand.name,
                    brand.status,
                )
            }
        }
    }
}
