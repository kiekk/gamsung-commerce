package com.loopers.domain.productlike

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.ProductSearchCondition
import com.loopers.domain.vo.Price
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun createProduct(command: ProductCommand.Create): ProductEntity {
        productRepository.findByBrandIdAndName(command.brandId, command.name)?.let {
            throw CoreException(ErrorType.CONFLICT, "이미 존재하는 상품입니다: ${command.name}")
        }
        return productRepository.createProduct(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): ProductEntity? {
        return productRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun searchProducts(condition: ProductSearchCondition, pageRequest: PageRequest): Page<ProductEntity> {
        // TODO: 나중에 QueryDSL로 변경해보자.
        val spec = Specification<ProductEntity> { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            condition.name?.let {
                predicates.add(cb.like(cb.lower(root.get("name")), "${it.lowercase()}%"))
            }

            condition.minPrice?.let {
                predicates.add(
                    cb.greaterThanOrEqualTo(root.get<Price>("price").get("value"), it),
                )
            }

            condition.maxPrice?.let {
                predicates.add(
                    cb.lessThanOrEqualTo(root.get<Price>("price").get("value"), it),
                )
            }

            cb.and(*predicates.toTypedArray())
        }
        return productRepository.findAll(spec, pageRequest)

    }

}
