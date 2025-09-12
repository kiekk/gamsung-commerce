package com.loopers.infrastructure.productrank

import com.loopers.domain.productrank.ProductRankHourly
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRankHourlyJpaRepository : JpaRepository<ProductRankHourly, Long>
