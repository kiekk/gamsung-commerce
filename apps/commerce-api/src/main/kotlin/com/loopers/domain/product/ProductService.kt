package com.loopers.domain.product

import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val cacheRepository: CacheRepository,
) {

    private val log = LoggerFactory.getLogger(ProductService::class.java)

    @Transactional
    fun createProduct(command: ProductCommand.Create): ProductEntity {
        productRepository.findByBrandIdAndName(command.brandId, command.name)?.let {
            throw CoreException(ErrorType.CONFLICT, "이미 존재하는 상품입니다: ${command.name}")
        }
        return productRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun findProductBy(id: Long): ProductEntity? {
        val cache = cacheRepository.get(CacheKey(CacheNames.PRODUCT_DETAIL_V1, id.toString()), ProductEntity::class.java)
        // 캐시가 존재
        cache?.let {
            log.info("[Cache Hit] Product: $cache")
            return it
        }
        val product = productRepository.findById(id)
        // 캐시가 저장
        product?.let {
            log.info("[Cache Miss] Product: $it")
            cacheRepository.set(CacheKey(CacheNames.PRODUCT_DETAIL_V1, id.toString()), it)
        }
        return product
    }

    @Transactional(readOnly = true)
    fun getProductsByIds(productIds: List<Long>): List<ProductEntity> {
        return productRepository.findByIds(productIds)
    }
}
