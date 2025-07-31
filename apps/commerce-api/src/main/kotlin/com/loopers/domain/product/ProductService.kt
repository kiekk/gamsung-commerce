package com.loopers.domain.product

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
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
        return productRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun findProductBy(id: Long): ProductEntity? {
        return productRepository.findById(id)
    }
}
