package com.loopers.domain.brand

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
) {

    @Transactional
    fun createBrand(brand: BrandEntity): BrandEntity {
        brandRepository.findByName(brand.name)?.let {
            throw CoreException(
                ErrorType.CONFLICT,
                "이미 존재하는 브랜드입니다: ${brand.name}",
            )
        }
        return brandRepository.save(brand)
    }

    @Transactional(readOnly = true)
    fun searchBrands(
        condition: BrandSearchCondition, pageRequest: PageRequest,
    ): Page<BrandEntity> {
        val spec = Specification<BrandEntity> { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            condition.name?.let {
                predicates.add(cb.like(cb.lower(root.get("name")), "${it.lowercase()}%"))
            }

            condition.status?.let {
                predicates.add(cb.equal(root.get<BrandEntity.BrandStatusType>("status"), it))
            }

            cb.and(*predicates.toTypedArray())
        }
        return brandRepository.findAll(spec, pageRequest)
    }

    @Transactional(readOnly = true)
    fun findBrandBy(brandId: Long): BrandEntity? {
        return brandRepository.findById(brandId)
    }

}
