package com.loopers.infrastructure.brand

import com.loopers.domain.brand.BrandEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BrandJpaRepository : JpaRepository<BrandEntity, Long>, JpaSpecificationExecutor<BrandEntity> {
    fun findByName(name: String): BrandEntity?
}
