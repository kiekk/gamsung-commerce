package com.loopers.domain.brand.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "brand")
class BrandView(
    @Id
    val id: Long,
    val name: String,
)
